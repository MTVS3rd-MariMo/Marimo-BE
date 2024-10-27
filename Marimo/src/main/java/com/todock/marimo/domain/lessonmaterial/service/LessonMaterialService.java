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
import org.springframework.context.annotation.Bean;
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

    // RestTemplate은 Spring에서 외부 서버와 HTTP 요청을 수행하는 데 사용됩니다. 이를 빈으로 등록해 재사용할 수 있게 설정합니다.
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Autowired
    private RestTemplate restTemplate;

    private final UserRepository userRepository;
    private final LessonMaterialRepository lessonMaterialRepository;


    @Autowired
    public LessonMaterialService(UserRepository userRepository, LessonMaterialRepository lessonMaterialRepository) {

        this.userRepository = userRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
    }

    /**
     * pdf 업로드
     */

    public String sendPdfToAiServer(MultipartFile pdfFile) {
        try {
            // 1. AI 서버 URI 설정
            String AIServerUrI = "URI";

            // 2. HttpHeaders 설정(멀티파트 형식 지정)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 3. PDF 파일을 멀티파트 형식을 Wrapping
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(pdfFile.getBytes()) {
                @Override
                public String getFilename() {
                    return pdfFile.getOriginalFilename(); // 파일 이름 설정
                }
            });

            // 4. HttpEntity 생성
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // 5. AI 서버로 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerUrI, request, String.class);

            // 6. AI 서버에서 받은 JSON 반환
            return response.getBody();
        } catch (Exception e) { // 예외 처리 로직 추가

            e.printStackTrace(); // 콘솔에 스택 트레이스 출력
            throw new RuntimeException("파일 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * 수업 자료 저장
     */

    @Transactional
    public LessonMaterial save(Long userId, LessonMaterialRegistRequestDto lessonMaterialInfo) {

        validateUserRole(userId); // 1. 선생님인지 검증
        validateRequestCounts(lessonMaterialInfo); // 2. 요청받은 수업자료 검증

        // 3. LessonMaterial 생성
        LessonMaterial lessonMaterial = new LessonMaterial(
                userId,
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
     * 유저 id로 수업 자료 전체 조회 (버튼에 보여줄 API)
     */


    /**
     * lessonMaterialId로 수업 자료 조회 (수정 페이지 들어가서 보여줄 API)
     */


    /**
     * 수업 자료 id로 수정 (수정 페이지에서 수정하고 요청)
     */

    @Transactional
    public void modifyLessonMaterial(Long lessonMaterialId) {

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