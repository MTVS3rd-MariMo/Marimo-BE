package com.todock.marimo.domain.lessonmaterial.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.LessonRole;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.SelectedQuiz;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LessonMaterialService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final LessonMaterialRepository lessonMaterialRepository;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper 추가

    @Autowired
    public LessonMaterialService(
            UserRepository userRepository,
            LessonMaterialRepository lessonMaterialRepository,
            RestTemplate restTemplate) {

        this.userRepository = userRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
        this.restTemplate = restTemplate;
    }


    /**
     * pdf 업로드
     */
    public LessonMaterialResponseDto sendPdfToAiServer(MultipartFile pdf, String pdfName) {
        try {

            // 1. AI 서버 URI 설정
            String AIServerUrI = "http://221.163.19.142:7993/pdfupload";

            // 2. HttpHeaders 설정(멀티파트 형식 지정)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 3. PDF 파일을 멀티파트 형식으로 Wrapping
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("pdf", new ByteArrayResource(pdf.getBytes()) {
                @Override
                public String getFilename() {
                    return pdf.getOriginalFilename(); // 파일 이름 설정
                }
            });

            // 4. HttpEntity 생성
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // 5. AI 서버로 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerUrI, request, String.class);

            log.info("AI 서버 응답: {}", response.getBody());

            // 6. AI 서버에서 받은 JSON 반환
            return parseLessonMaterialJson(response.getBody(), pdfName);


        } catch (Exception e) { // 예외 처리 로직 추가
            throw new RuntimeException("파일 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * 수업 자료 저장
     */
    @Transactional
    public LessonMaterial save(LessonMaterialDto lessonMaterialInfo) {
        validateUserRole(lessonMaterialInfo.getUserId());
        validateRequestCounts(lessonMaterialInfo); // 여기서 한번에 검증

        LessonMaterial lessonMaterial = new LessonMaterial(
                lessonMaterialInfo.getUserId(),
                lessonMaterialInfo.getBookTitle(),
                lessonMaterialInfo.getBookContents()
        );

        // 모든 질문을 한번에 추가
        List<OpenQuestion> questions = lessonMaterialInfo.getOpenQuestionList().stream()
                .map(q -> new OpenQuestion(lessonMaterial, q.getQuestionTitle()))
                .collect(Collectors.toList());
        lessonMaterial.setOpenQuestionList(questions);

        // 퀴즈 한번에 추가
        SelectedQuiz selectedQuiz = new SelectedQuiz(lessonMaterial);
        List<Quiz> quizList = lessonMaterialInfo.getQuizzeList().stream()
                .map(quizRequest -> new Quiz(
                        quizRequest.getQuestion(),
                        quizRequest.getAnswer(),
                        quizRequest.getChoices1(),
                        quizRequest.getChoices2(),
                        quizRequest.getChoices3(),
                        quizRequest.getChoices4()
                ))
                .collect(Collectors.toList());
        selectedQuiz.setQuizList(quizList);  // 퀴즈 리스트 한번에 설정
        lessonMaterial.getSelectedQuizList().add(selectedQuiz);

        // 역할 4개 한번에 추가
        List<LessonRole> roles = lessonMaterialInfo.getRoleList().stream()
                .map(roleRequest -> new LessonRole(null, roleRequest.getRoleName()))
                .collect(Collectors.toList());
        lessonMaterial.setLessonRoleList(roles);

        // DB에 저장
        return lessonMaterialRepository.save(lessonMaterial);
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회
     */
    public List<LessonMaterialNameResponseDto> getLessonMaterialByUserId(Long userId) {
        validateUserRole(userId);
        return lessonMaterialRepository.findByUserId(userId)
                .stream()
                .map(lm -> new LessonMaterialNameResponseDto(lm.getBookTitle(), lm.getBookContents()))
                .collect(Collectors.toList());
    }


    /**
     * lessonMaterialId로 수업 자료 내용 상세 조회
     */
    public LessonMaterial getLessonMaterialByLessonMaterialId(Long lessonMaterialId) {

        return lessonMaterialRepository.findLessonMaterialByLessonMaterialId(lessonMaterialId);

    }


    /**
     * 수업 자료를 수업자료 id로 수정 (수정 페이지에서 수정하고 요청)
     */
    @Transactional
    public void updateLessonMaterial(Long lessonMaterialId, LessonMaterialDto updateDto) {

        // 1. 기존 수업 자료 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수업 자료입니다."));

        // 2. 기본 정보 업데이트
        lessonMaterial.setBookTitle(updateDto.getBookTitle());
        lessonMaterial.setBookContents(updateDto.getBookContents());

        // 3. 기존 데이터 초기화
        lessonMaterial.getOpenQuestionList().clear();
        lessonMaterial.getSelectedQuizList().clear();
        lessonMaterial.getLessonRoleList().clear();

        // 4. 열린 질문 업데이트
        updateDto.getOpenQuestionList().forEach(questionRequest -> {
            OpenQuestion openQuestion = new OpenQuestion(lessonMaterial, questionRequest.getQuestionTitle());
            lessonMaterial.addOpenQuestion(openQuestion);
        });

        // 5. 퀴즈 업데이트
        lessonMaterial.getSelectedQuizList().clear(); // 기존에 있던 선택된 퀴즈 삭제

        SelectedQuiz selectedQuiz = new SelectedQuiz(lessonMaterial);
        List<Quiz> quizList = updateDto.getQuizzeList().stream()
                .map(quizRequest -> new Quiz(
                        quizRequest.getQuestion(),
                        quizRequest.getAnswer(),
                        quizRequest.getChoices1(),
                        quizRequest.getChoices2(),
                        quizRequest.getChoices3(),
                        quizRequest.getChoices4()
                ))
                .toList();
        selectedQuiz.addQuiz(quizList.get(0), quizList.get(1));
        lessonMaterial.addSelectedQuiz(selectedQuiz);

        // 6. 역할 업데이트
        updateDto.getRoleList().forEach(roleRequest -> {
            LessonRole lessonRole = new LessonRole(null, roleRequest.getRoleName());
            lessonMaterial.addRole(lessonRole);
        });

        // 7. 요청 데이터 검증
        validateRequestCounts(updateDto);

        // 8. 저장
        lessonMaterialRepository.save(lessonMaterial);
    }

    // AI 반환 Json 파싱 메서드
    private LessonMaterialResponseDto parseLessonMaterialJson(String jsonResponse, String pdfName) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);

        // LessonMaterial 객체 생성
        LessonMaterial lessonMaterial = new LessonMaterial();
        lessonMaterial.setUserId(1L); // 임시 userId 생성
        lessonMaterial.setBookTitle(pdfName);
        lessonMaterial.setBookContents(root.path("pdftext").asText("")); // 기본값을 빈 문자열로 설정

        // 열린 질문 생성 및 추가
        List<OpenQuestion> openQuestions = new ArrayList<>();
        JsonNode openQuestionsNode = root.path("open_questions");
        if (!openQuestionsNode.isMissingNode()) { // 노드가 존재할 때만 진행
            openQuestionsNode.forEach(questionNode -> {
                OpenQuestion question = new OpenQuestion(lessonMaterial, questionNode.path("question").asText(""));
                openQuestions.add(question);
            });
        }
        lessonMaterial.setOpenQuestionList(openQuestions);

        // 퀴즈 생성 및 추가
        List<Quiz> quizList = new ArrayList<>();
        JsonNode quizNode = root.path("quiz");
        if (!quizNode.isMissingNode()) { // 노드가 존재할 때만 진행
            quizNode.forEach(qNode -> {
                Quiz quiz = new Quiz(
                        qNode.path("question").asText(""),
                        qNode.path("answer").asInt(),
                        qNode.path("choices1").asText(""),
                        qNode.path("choices2").asText(""),
                        qNode.path("choices3").asText(""),
                        qNode.path("choices4").asText("")
                );
                quizList.add(quiz);
            });
        }
        SelectedQuiz selectedQuiz = new SelectedQuiz(lessonMaterial);
        selectedQuiz.setQuizList(quizList);
        lessonMaterial.getSelectedQuizList().add(selectedQuiz);

        // 역할 생성 및 추가
        List<LessonRole> roles = new ArrayList<>();
        JsonNode charactersNode = root.path("characters");
        if (!charactersNode.isMissingNode()) { // 노드가 존재할 때만 진행
            charactersNode.forEach(characterNode -> {
                roles.add(new LessonRole(null, characterNode.asText("")));
            });
        }
        lessonMaterial.setLessonRoleList(roles);

        // LessonMaterial 저장
        lessonMaterialRepository.save(lessonMaterial);

        // 클라이언트에 반환할 LessonMaterialResponseDto 생성
        return new LessonMaterialResponseDto(quizList, openQuestions);
    }


    /**
     * 수업 자료 id로 수업 자료 삭제
     */
    @Transactional
    public void deleteById(Long lessonMaterialId) {
        lessonMaterialRepository.deleteById(lessonMaterialId);
    }


    /**
     * 선생님인지 검증
     */
    private void validateUserRole(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 선생님입니다."));

        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("선생님만 수업 자료를 만들 수 있습니다.");
        }
    }


    /**
     * 컨텐츠 개수 검증
     */
    private void validateRequestCounts(LessonMaterialDto requestDto) {
        if (requestDto.getOpenQuestionList().size() != 2) {
            throw new IllegalArgumentException("열린 질문은 2개여야 합니다.");
        }
        if (requestDto.getQuizzeList().size() != 2) {
            throw new IllegalArgumentException("퀴즈는 2개여야 합니다.");
        }
        if (requestDto.getRoleList().size() != 4) {
            throw new IllegalArgumentException("역할은 4개여야 합니다.");
        }
    }

}