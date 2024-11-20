package com.todock.marimo.domain.lessonmaterial.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lessonmaterial.dto.*;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.OpenQuestionRequestDto;
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
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LessonMaterialService {

    @Value("${external.api.lesson-material-server-url}")
    private String AIServerURL;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final LessonMaterialRepository lessonMaterialRepository;

    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱용 ObjectMapper 추가

    @Autowired
    public LessonMaterialService(
            LessonMaterialRepository lessonMaterialRepository,
            UserRepository userRepository,
            RestTemplate restTemplate) {

        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
    }


    /**
     * pdf 업로드
     */
    @Transactional
    public LessonMaterialResponseDto sendPdfToAiServer(
            MultipartFile pdf, Long userId, String bookTitle, String author) {

        validateUserRole(userId);

        if (pdf == null || pdf.isEmpty()) {
            throw new IllegalArgumentException("업로드할 PDF 파일이 제공되지 않았습니다.");
        }

        List<LessonMaterial> lessonMaterials = lessonMaterialRepository.findByUserId(userId);
        if (lessonMaterials.size() >= 3) {
            throw new IllegalStateException("더이상 수업 자료를 만들 수 없습니다. 최대 3개까지 가능합니다.");
        }

        try {

            // 1. AI 서버 URI 설정
            log.info("AI 서버 URI 설정: {}", AIServerURL);

            // 2. HttpHeaders 설정(멀티파트 형식 지정)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 3. PDF 파일을 멀티파트 형식으로 Wrapping
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("pdf", new ByteArrayResource(pdf.getBytes()) {
                @Override
                public String getFilename() {
                    return pdf.getOriginalFilename();
                }
            });

            // 4. HttpEntity 생성
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            // 5. AI 서버로 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerURL, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("AI 서버와의 통신 중 오류가 발생했습니다. 응답 코드: " + response.getStatusCode());
            }


            // 6. AI 서버에서 받은 JSON 반환
            return parseLessonMaterialJson(userId, response.getBody(), bookTitle, author);

        } catch (Exception e) {
            log.error("파일 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * pdf에서 바로 받은 후 열린질문, 퀴즈 2개 선택해서 수정 - 기본 수정도 포함
     */
    @Transactional
    public void updateLessonMaterial(LessonMaterialRequestDto lessonMaterialInfo) {

        if (lessonMaterialInfo == null || lessonMaterialInfo.getLessonMaterialId() == null) {
            throw new IllegalArgumentException("수정할 수업 자료 정보가 제공되지 않았습니다.");
        }

        LessonMaterial lessonMaterial = lessonMaterialRepository
                .findById(lessonMaterialInfo.getLessonMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수업 자료입니다: " + lessonMaterialInfo.getLessonMaterialId()));

        List<QuizDto> quizzes = lessonMaterialInfo.getQuizList();

        if (quizzes != null && !quizzes.isEmpty()) {
            lessonMaterial.getQuizList().clear();

            for (QuizDto quizDto : quizzes) {
                if (!isValidQuiz(quizDto)) {
                    throw new IllegalArgumentException("유효하지 않은 퀴즈 정보가 포함되어 있습니다.");
                }

                Quiz quiz = new Quiz(
                        lessonMaterial,
                        quizDto.getQuestion(),
                        quizDto.getAnswer(),
                        quizDto.getChoices1(),
                        quizDto.getChoices2(),
                        quizDto.getChoices3(),
                        quizDto.getChoices4()
                );
                lessonMaterial.getQuizList().add(quiz);
            }
        } else {
            log.info("퀴즈가 존재하지 않습니다.");
        }

        List<OpenQuestionRequestDto> openQuestions = lessonMaterialInfo.getOpenQuestionList();
        if (openQuestions != null && !openQuestions.isEmpty()) {
            lessonMaterial.getOpenQuestionList().clear();

            for (OpenQuestionRequestDto oqDto : openQuestions) {
                if (oqDto.getQuestionTitle() == null || oqDto.getQuestionTitle().trim().isEmpty()) {
                    throw new IllegalArgumentException("유효하지 않은 열린 질문이 포함되어 있습니다.");
                }

                OpenQuestion openQuestion = new OpenQuestion(
                        lessonMaterial,
                        oqDto.getQuestionTitle()
                );
                lessonMaterial.getOpenQuestionList().add(openQuestion);
            }
        } else {
            log.info("열린 질문이 존재하지 않습니다.");
        }

        lessonMaterialRepository.save(lessonMaterial);
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회
     */
    public List<LessonMaterialNameResponseDto> getLessonMaterialByUserId(Long userId) {

        validateUserRole(userId); // 선생님 검증

        List<LessonMaterialNameResponseDto> lessonMaterialNameList = lessonMaterialRepository.findByUserId(userId)
                .stream()
                .map(dto -> new LessonMaterialNameResponseDto(dto.getLessonMaterialId(), dto.getBookTitle()))
                .collect(Collectors.toList());
        return lessonMaterialNameList.isEmpty() ? Collections.emptyList() : lessonMaterialNameList;
    }


    /**
     * lessonMaterialId로 수업자료 수정 상세 조회
     */
    public DetailLessonMaterialDto findById(Long lessonMaterialId) {

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("lessonMaterialId로 수업자료를 조회할 수 없습니다."));

        // OpenQuestions 매핑
        List<OpenQuestionUpdateDto> openQuestions = lessonMaterial.getOpenQuestionList().stream()
                .map(openQuestion -> new OpenQuestionUpdateDto(
                        openQuestion.getOpenQuestionId(),
                        openQuestion.getQuestion()))
                .toList();

        // Quizzes 매핑
        List<QuizDto> quizList = lessonMaterial.getQuizList().stream()
                .map(quiz -> new QuizDto(
                        quiz.getQuizId(),
                        quiz.getQuestion(),
                        quiz.getAnswer(),
                        quiz.getChoices1(),
                        quiz.getChoices2(),
                        quiz.getChoices3(),
                        quiz.getChoices4()
                ))
                .toList();

        // DTO 생성 및 반환
        return new DetailLessonMaterialDto(
                lessonMaterialId,
                openQuestions,
                quizList
        );
    }





    /**
     * 수업 자료 id로 수업 자료 삭제
     */
    @Transactional
    public void deleteById(Long lessonMaterialId) {
        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() -> new EntityNotFoundException("lessonMaterialId로 수업자료를 찾을 수 없습니다."));

        lessonMaterial.setUserId(null); // 유저 Id를 없애서 매칭 안되도록 함
        log.info("lessonMaterial.userId : {}", lessonMaterial.getUserId());
    }


    /**
     * AI 반환 Json 파싱 메서드
     */
    private LessonMaterialResponseDto parseLessonMaterialJson(Long userId, String jsonResponse, String bookTitle, String author) {

        JsonNode root = null;
        try {
            root = objectMapper.readTree(jsonResponse);

            // LessonMaterial 객체 생성
            LessonMaterial lessonMaterial = new LessonMaterial(
                    userId,
                    bookTitle,
                    root.path("pdftext").asText(""),
                    author
            );


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
            log.info("생성된 lessonMaterialId() : {}", lessonMaterial.getLessonMaterialId());

            // 열린 질문을 OpenQuestionDto로 변환하여 클라이언트에 보낼 준비
            List<OpenQuestionResponseDto> openQuestionDtos = new ArrayList<>();

            JsonNode openQuestionsNode = root.path("open_questions"); //  라는 JSON에서 open_questions 노드 찾기

            // 열린 질문 파싱
            if (!openQuestionsNode.isMissingNode()) { // 있으면
                openQuestionsNode.forEach(questionNode -> {
                    String questionText = questionNode.path("question").asText(""); // question 노드 찾아서 추출
                    if (!questionText.isEmpty()) {  // 빈 문자열 체크 추가
                        OpenQuestionResponseDto openQuestionResponseDto = new OpenQuestionResponseDto(
                                questionText
                        );

                        openQuestionDtos.add(openQuestionResponseDto);
                    }
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 변형이 잘못되었습니다.");
        }
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
     * 퀴즈 수정 시 검증
     */
    private boolean isValidQuiz(QuizDto quizDto) {
        return quizDto != null &&
                quizDto.getQuestion() != null && !quizDto.getQuestion().trim().isEmpty() &&
                quizDto.getChoices1() != null && !quizDto.getChoices1().trim().isEmpty() &&
                quizDto.getChoices2() != null && !quizDto.getChoices2().trim().isEmpty() &&
                quizDto.getChoices3() != null && !quizDto.getChoices3().trim().isEmpty() &&
                quizDto.getChoices4() != null && !quizDto.getChoices4().trim().isEmpty();
    }

}