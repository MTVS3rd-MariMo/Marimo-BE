package com.todock.marimo.domain.lessonmaterial.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lessonmaterial.dto.*;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.LessonRole;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
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
    @Transactional
    public LessonMaterialResponseDto sendPdfToAiServer(MultipartFile pdf, String pdfName) {
        try {

            // 1. AI 서버 URI 설정
            String AIServerUrI = "http://metaai2.iptime.org:7993/pdfupload";

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

            // 확인용 응답 로그
            log.info("AI 서버 응답: {}", response.getBody());

            // 6. AI 서버에서 받은 JSON 반환
            return parseLessonMaterialJson(response.getBody(), pdfName);

        } catch (Exception e) { // 예외 처리 로직 추가
            throw new RuntimeException("파일 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * AI 반환 Json 파싱 메서드
     */
    private LessonMaterialResponseDto parseLessonMaterialJson(String jsonResponse, String pdfName) throws Exception {

        JsonNode root = objectMapper.readTree(jsonResponse);

        // LessonMaterial 객체 생성
        LessonMaterial lessonMaterial = new LessonMaterial();
        lessonMaterial.setUserId(1L);// 임시 userId 생성
        //lessonMaterial.setUserId(lessonMaterial.getUserId());
        lessonMaterial.setBookTitle(pdfName); // 책 제목
        lessonMaterial.setBookContents(root.path("pdftext").asText("")); // 책 내용

        // 역할 생성 및 추가
        List<LessonRole> roles = new ArrayList<>();
        JsonNode charactersNode = root.path("characters");
        if (!charactersNode.isMissingNode()) { // 노드가 존재할 때만 진행
            charactersNode.forEach(characterNode -> {
                roles.add(new LessonRole(lessonMaterial, characterNode.asText("")));
            });
        }

        lessonMaterial.setLessonRoleList(roles);

        // LessonMaterial 저장 (열린 질문, 퀴즈는 저장하지 않음)
        lessonMaterialRepository.save(lessonMaterial);

        // 클라이언트에 반환할 LessonMaterialResponseDto 생성

        // 열린 질문을 OpenQuestionDto로 변환하여 클라이언트에 보낼 준비
        List<OpenQuestionResponseDto> openQuestionDtos = new ArrayList<>();
        JsonNode openQuestionsNode = root.path("open_questions");
        if (!openQuestionsNode.isMissingNode()) {
            openQuestionsNode.forEach(questionNode -> {
                openQuestionDtos.add(new OpenQuestionResponseDto(questionNode.path("question").asText("")));
            });
        }

        // 퀴즈를 QuizDto로 변환하여 클라이언트에 보낼 준비
        List<QuizDto> quizDtos = new ArrayList<>();
        JsonNode quizNode = root.path("quiz");
        if (!quizNode.isMissingNode()) {
            final AtomicLong quizIdCounter = new AtomicLong(1); // quizId를 1부터 시작하도록 설정
            quizNode.forEach(qNode -> {
                quizDtos.add(new QuizDto(
                        quizIdCounter.getAndIncrement(),
                        qNode.path("question").asText(""),
                        qNode.path("answer").asInt(),
                        qNode.path("choices1").asText(""),
                        qNode.path("choices2").asText(""),
                        qNode.path("choices3").asText(""),
                        qNode.path("choices4").asText("")
                ));
            });
        }

        // 클라이언트에 반환할 LessonMaterialResponseDto 생성
        return new LessonMaterialResponseDto(lessonMaterial.getLessonMaterialId(), quizDtos, openQuestionDtos);

    }


    /**
     * pdf에서 바로 받은 후 열린질문, 퀴즈 2개 선택해서 수정
     */
    @Transactional
    public void updateLessonMaterial(LessonMaterialRequestDto lessonMaterialInfo) {

        // validateUserRole(lessonMaterialInfo.getUserId());
        // validateRequestCounts(lessonMaterialInfo); // 여기서 한번에 검증

        LessonMaterial lessonMaterial = lessonMaterialRepository
                .findById(lessonMaterialInfo.getLessonMaterialId()).orElse(null);
        if (lessonMaterial == null) {  // lessonMaterial이 null인지 확인
            throw new IllegalArgumentException("존재하지 않는 수업 자료입니다: " + lessonMaterialInfo.getLessonMaterialId());
        }

        //List<QuizDto> quizList = lessonMaterialInfo.getQuizzes();
        //List<OpenQuestionDto> openQuestionList = lessonMaterialInfo.getOpenQuestions();


        // 모든 질문을 한번에 추가
        lessonMaterial.setOpenQuestionList(createOpenQuestionreList
                (lessonMaterial, lessonMaterialInfo.getOpenQuestions()));

        // createQuizList 메서드에서 생성된 퀴즈 리스트의 각 항목을 lessonMaterial의 quizList에 추가
        List<Quiz> createdQuizList = createQuizreList(lessonMaterial, lessonMaterialInfo.getQuizzes());
        lessonMaterial.getQuizList().addAll(createdQuizList); // 전체 리스트를 개별 항목으로 추가


        // DB에 저장
        lessonMaterialRepository.save(lessonMaterial);
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회
     */
    public List<LessonMaterialNameResponseDto> getLessonMaterialByUserId(Long userId) {
        validateUserRole(userId);
        return lessonMaterialRepository.findByUserId(userId)
                .stream()
                .map(dto -> new LessonMaterialNameResponseDto(dto.getLessonMaterialId(), dto.getBookTitle()))
                .collect(Collectors.toList());
    }


    /**
     * lessonMaterialId로 수업 자료 내용 상세 조회
     */
    public Optional<LessonMaterial> findById(Long lessonMaterialId) {

        return lessonMaterialRepository.findById(lessonMaterialId);

    }

    /**
     * 수업 중 학생용 lessonMaterialId로 수업용 수업 상세 자료 조회
     */
    public StudentLessonMaterialDto getLessonMaterialById(Long lessonMaterialId) {

        // lessonMaterial 조회
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("LessonMaterial not found with id: " + lessonMaterialId));

        // openQuestions 변환
        List<OpenQuestionResponseDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(openQuestion -> new OpenQuestionResponseDto(openQuestion.getQuestion()))
                .toList();

        // quizzes 변환
        List<LessonQuizDto> quizzes = lessonMaterial.getQuizList().stream()
                .map(quiz -> new LessonQuizDto(
                        quiz.getQuestion(),
                        quiz.getAnswer(),
                        quiz.getChoices1(),
                        quiz.getChoices2(),
                        quiz.getChoices3(),
                        quiz.getChoices4()
                ))
                .toList();

        // lessonRoles 변환
        // lessonRoles를 List<LessonRoleDto> -> List<String>으로 변환
        List<String> lessonRoles = lessonMaterial.getLessonRoleList().stream()
                .map(LessonRole::getRoleName) // LessonRole 객체의 역할 이름을 추출
                .toList();

        // TeacherLessonMaterialDto 생성 및 반환
        return new StudentLessonMaterialDto(
                lessonMaterial.getBookTitle(),
                lessonMaterial.getBookContents(),
                quizzes,
                openQuestions,
                lessonRoles
        );
    }

    /**
     * 수업 자료를 수업자료 id로 수정 (수정 페이지에서 수정하고 요청)
     */


    /**
     * 수업 자료 id로 수업 자료 삭제
     */
    @Transactional
    public void deleteById(Long lessonMaterialId) {
        lessonMaterialRepository.deleteById(lessonMaterialId);
    }


    // OpenQuestion 리스트 생성 헬퍼 메서드
    private List<OpenQuestion> createOpenQuestionList(LessonMaterial lessonMaterial, List<OpenQuestionDto> openQuestionDtos) {
        if (openQuestionDtos == null) {  // null 체크 추가
            return new ArrayList<>();
        }
        return openQuestionDtos.stream()
                .map(q -> new OpenQuestion(lessonMaterial, q.getQuestion()))
                .collect(Collectors.toList());
    }

    // Quiz 리스트 생성 헬퍼 메서드
    private List<Quiz> createQuizList(LessonMaterial lessonMaterial, List<QuizDto> questionDtos) {
        if (questionDtos == null) {  // null 체크 추가
            return new ArrayList<>();
        }
        return questionDtos.stream()
                .map(quiz -> new Quiz())
                .collect(Collectors.toList());
    }

    // ReOpenQuestion 리스트 생성 헬퍼 메서드
    private List<OpenQuestion> createOpenQuestionreList(LessonMaterial lessonMaterial, List<OpenQuestionRequestDto> openQuestionDtos) {
        if (openQuestionDtos == null) {  // null 체크 추가
            return new ArrayList<>();
        }
        return openQuestionDtos.stream()
                .map(q -> new OpenQuestion(lessonMaterial, q.getQuestionTitle()))
                .collect(Collectors.toList());
    }

    // ReQuiz 리스트 생성 헬퍼 메서드
    private List<Quiz> createQuizreList(LessonMaterial lessonMaterial, List<QuizRequestDto> questionDtos) {
        if (questionDtos == null) {  // null 체크 추가
            return new ArrayList<>();
        }
        return questionDtos.stream()
                .map(quiz -> new Quiz())
                .collect(Collectors.toList());
    }

    // LessonRole 리스트 생성 헬퍼 메서드
    private List<LessonRole> createLessonRoleList(List<LessonRoleDto> roleDto) {
        return roleDto.stream()
                .map(roleRequest -> new LessonRole(null, roleRequest.getCharacter()))
                .collect(Collectors.toList());
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
    /*private void validateRequestCounts(LessonMaterialDto requestDto) {
        if (requestDto.getOpenQuestionList().size() != 2) {
            throw new IllegalArgumentException("열린 질문은 2개여야 합니다.");
        }
        if (requestDto.getQuizzeList().size() != 2) {
            throw new IllegalArgumentException("퀴즈는 2개여야 합니다.");
        }
        if (requestDto.getRoleList().size() != 4) {
            throw new IllegalArgumentException("역할은 4개여야 합니다.");
        }
    }*/

}