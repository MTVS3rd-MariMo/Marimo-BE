package com.todock.marimo.domain.lessonmaterial.service;

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

            // 3. PDF 파일을 멀티파트 형식을 Wrapping
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

        validateUserRole(lessonMaterialInfo.getUserId()); // 1. 선생님인지 검증
        validateRequestCounts(lessonMaterialInfo); // 2. 요청받은 수업자료 검증

        // 3. LessonMaterial 생성
        LessonMaterial lessonMaterial = new LessonMaterial(
                lessonMaterialInfo.getUserId(),
                lessonMaterialInfo.getBookTitle(),
                lessonMaterialInfo.getBookContents()
        );

        // 4. 열린 질문 추가
        lessonMaterialInfo.getOpenQuestionList().forEach(questionRequest -> {
            OpenQuestion question = new OpenQuestion(lessonMaterial, questionRequest.getQuestionTitle());
            lessonMaterial.addOpenQuestion(question);
        });

        // 5. 퀴즈 추가
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
                .toList();

        // 두 개의 퀴즈를 한 번에 추가
        selectedQuiz.addQuiz(quizList.get(0), quizList.get(1));
        lessonMaterial.addSelectedQuiz(selectedQuiz);

        // 6. 역할 추가
        lessonMaterialInfo.getRoleList().forEach(roleRequest -> {

            LessonRole lessonRole = new LessonRole(null, roleRequest.getRoleName());

            lessonMaterial.addRole(lessonRole);

        });

        // DB에 저장
        return lessonMaterialRepository.save(lessonMaterial);
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회 (pdf 이름만 보여줌)
     */

    public List<LessonMaterial> getLessonMaterialByUserId(Long userId) {

        validateUserRole(userId);
        List<LessonMaterial> lessonMaterialList = lessonMaterialRepository.findByUserId(userId);

        return lessonMaterialList;
    }


    /**
     * lessonMaterialId로 수업 자료 내용 조회 (작성한 내용 조회)
     */

    public LessonMaterial getLessonMaterialByLessonMaterialId(Long userId) {

        validateUserRole(userId);

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(userId).orElse(null);

        return lessonMaterial;


    }

    /**
     * 수업 자료를 수업자료 id로 수정 (수정 페이지에서 수정하고 요청)
     */

    @Transactional
    public void updateLessonMaterial(
            Long lessonMaterialId,
            LessonMaterialRegistRequestDto lessonMaterialRegistRequestDto) {

        // 1. 수업 자료 부터 조회
        LessonMaterial foundLessonMaterial = lessonMaterialRepository
                .findById(lessonMaterialId)
                .orElseThrow(IllegalAccessError::new);

        // 전체 수정 코드


    }

    /**
     * 수업 자료 id로 수업 자료 삭제
     */
    public void deleteById(Long lessonMaterialId) {
        lessonMaterialRepository.deleteById(lessonMaterialId);
    }


    /**
     * 선생님인지 검증
     */
    private void validateUserRole(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalAccessError("존재하지 않는 선생님입니다."));

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