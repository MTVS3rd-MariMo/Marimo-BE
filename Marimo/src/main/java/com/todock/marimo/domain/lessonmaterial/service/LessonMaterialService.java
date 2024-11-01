package com.todock.marimo.domain.lessonmaterial.service;

import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialRegistRequestDto;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonMaterialService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final LessonMaterialRepository lessonMaterialRepository;

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
    public String sendPdfToAiServer(MultipartFile pdf) {
        try {
            // 1. AI 서버 URI 설정
            String AIServerUrI = "http://metaai2.iptime.org:64987/pdfupload";

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

            // 6. AI 서버에서 받은 JSON 반환
            return response.getBody();

        } catch (Exception e) { // 예외 처리 로직 추가
            throw new RuntimeException("파일 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * 수업 자료 저장
     */
    @Transactional
    public LessonMaterial save(LessonMaterialRegistRequestDto lessonMaterialInfo) {
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
                        quizRequest.getFirstChoice(),
                        quizRequest.getSecondChoice(),
                        quizRequest.getThirdChoice(),
                        quizRequest.getFourthChoice()
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
    public void updateLessonMaterial(Long lessonMaterialId, LessonMaterialRegistRequestDto updateDto) {
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
        SelectedQuiz selectedQuiz = new SelectedQuiz(lessonMaterial);
        List<Quiz> quizList = updateDto.getQuizzeList().stream()
                .map(quizRequest -> new Quiz(
                        quizRequest.getQuestion(),
                        quizRequest.getAnswer(),
                        quizRequest.getFirstChoice(),
                        quizRequest.getSecondChoice(),
                        quizRequest.getThirdChoice(),
                        quizRequest.getFourthChoice()
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
    private void validateRequestCounts(LessonMaterialRegistRequestDto requestDto) {
        if (requestDto.getOpenQuestionList().size() != 3) {
            throw new IllegalArgumentException("열린 질문은 3개여야 합니다.");
        }
        if (requestDto.getQuizzeList().size() != 2) {
            throw new IllegalArgumentException("퀴즈는 2개여야 합니다.");
        }
        if (requestDto.getRoleList().size() != 4) {
            throw new IllegalArgumentException("역할은 4개여야 합니다.");
        }
    }

}