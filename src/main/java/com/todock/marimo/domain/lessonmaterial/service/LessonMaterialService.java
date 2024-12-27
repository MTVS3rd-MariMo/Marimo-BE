package com.todock.marimo.domain.lessonmaterial.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todock.marimo.domain.lessonmaterial.dto.*;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.LessonMaterialResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.reponse.RoleResponseDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.LessonMaterialRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.request.OpenQuestionRequestDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.entity.LessonRole;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.lessonmaterial.repository.LessonRoleRepository;
import com.todock.marimo.domain.lessonmaterial.repository.OpenQuestionRepository;
import com.todock.marimo.domain.lessonmaterial.repository.QuizRepository;
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

    // 문제
    @Value("${external.api.lesson-material-server-url}")
    private String AIServerURL;

    private final ObjectMapper objectMapper; // JSON 파싱용 ObjectMapper 추가
    private final RestTemplate restTemplate;
    private final QuizRepository quizRepository;
    private final LessonRoleRepository lessonRoleRepository;
    private final OpenQuestionRepository openQuestionRepository;
    private final LessonMaterialRepository lessonMaterialRepository;

    @Autowired
    public LessonMaterialService(
            LessonMaterialRepository lessonMaterialRepository,
            OpenQuestionRepository openQuestionRepository,
            LessonRoleRepository lessonRoleRepository,
            QuizRepository quizRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {

        this.lessonMaterialRepository = lessonMaterialRepository;
        this.openQuestionRepository = openQuestionRepository;
        this.lessonRoleRepository = lessonRoleRepository;
        this.quizRepository = quizRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    /**
     * pdf 업로드
     */
    @Transactional
    public LessonMaterialResponseDto sendPdfToAiServer(
            MultipartFile pdf, Long userId, String bookTitle, String author) {

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
            MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>(); // 멀티파트타입 전달은 MultiValueMap 사용
            bodyMap.add("pdf", new ByteArrayResource(pdf.getBytes()) {
                @Override
                public String getFilename() {
                    return pdf.getOriginalFilename(); // 파일 이름 설정
                }
            });

            // 4. HttpEntity 생성
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(bodyMap, headers);

            // 5. AI 서버로 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(AIServerURL, request, String.class);

            // 6. AI 서버에서 받은 JSON 반환
            return parseLessonMaterialJson(userId, response.getBody(), bookTitle, author);

        } catch (Exception e) {

            log.error("AI서버에 pdf 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("AI서버에 pdf 전송 중 오류 발생: " + e.getMessage());
        }
    }


    /**
     * pdf에서 바로 받은 후 열린질문, 퀴즈 2개 선택해서 수정 후 저장- 기본 수정도 포함
     */
    @Transactional
    public void updateLessonMaterial(LessonMaterialRequestDto lessonMaterialInfo) {

        log.info(lessonMaterialInfo.toString());

        LessonMaterial lessonMaterial = lessonMaterialRepository
                .findById(lessonMaterialInfo.getLessonMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 수업 자료입니다: " + lessonMaterialInfo.getLessonMaterialId()));

        List<QuizDto> quizzes = lessonMaterialInfo.getQuizList();

        if (quizzes != null && !quizzes.isEmpty()) {
            // 기존 퀴즈 데이터를 명시적으로 삭제 (수정된 부분)
            quizRepository.deleteAll(lessonMaterial.getQuizList()); // 기존 데이터 DB에서 삭제
            lessonMaterial.getQuizList().clear(); // 메모리 상에서도 삭제

            for (QuizDto quizDto : quizzes) {

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
        }

        List<OpenQuestionRequestDto> openQuestions = lessonMaterialInfo.getOpenQuestionList();
        if (openQuestions != null && !openQuestions.isEmpty()) {
            // 기존 열린 질문 데이터를 명시적으로 삭제 (수정된 부분)
            openQuestionRepository.deleteAll(lessonMaterial.getOpenQuestionList()); // 기존 데이터 DB에서 삭제
            lessonMaterial.getOpenQuestionList().clear(); // 메모리 상에서도 삭제

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

            // 책 내용 수정
            lessonMaterial.setBookContents(lessonMaterialInfo.getBookContents());

            // 역할 리스트 수정
            List<RoleResponseDto> roles = lessonMaterialInfo.getRoleList();

            for (RoleResponseDto role : roles) {
                LessonRole lessonRole = lessonRoleRepository.findById(role.getRoleId()).orElseThrow(() ->
                        new EntityNotFoundException("역할을 찾을 수 없습니다."));
                lessonRole.setRoleName(role.getRoleName());
                lessonRoleRepository.save(lessonRole);
            }
        } else {
            log.info("열린 질문이 존재하지 않습니다.");
        }

        lessonMaterialRepository.save(lessonMaterial);
    }


    /**
     * lessonMaterialId로 수업자료 상세 조회 - 수정
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

        // 저장된 역할을 RoleResponseDto로 변환
        List<RoleResponseDto> roleList = lessonMaterial.getLessonRoleList().stream()
                .map(role -> new RoleResponseDto(
                        role.getRoleId(),
                        role.getRoleName()
                ))
                .collect(Collectors.toList());

        log.info(roleList.toString());

        // DTO 생성 및 반환
        return new DetailLessonMaterialDto(
                lessonMaterialId,
                lessonMaterial.getBookContents(),
                roleList,
                openQuestions,
                quizList
        );
    }


    /**
     * 유저 id로 유저의 수업 자료 전체 조회
     */
    public List<LessonMaterialNameResponseDto> getLessonMaterialByUserId(Long userId) {

        List<LessonMaterialNameResponseDto> lessonMaterialNameList = lessonMaterialRepository.findByUserId(userId)
                .stream()
                .map(dto ->
                        new LessonMaterialNameResponseDto(
                                dto.getLessonMaterialId(),
                                dto.getBookTitle()
                        )
                )
                .collect(Collectors.toList());

        // 비어있어도 반환
        return lessonMaterialNameList.isEmpty() ? Collections.emptyList() : lessonMaterialNameList;
    }


    /**
     * 수업 자료 id로 수업 자료 삭제
     */
    @Transactional
    public void deleteById(Long lessonMaterialId) {

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lessonMaterialId)
                .orElseThrow(() ->
                        new EntityNotFoundException("lessonMaterialId가 " + lessonMaterialId + "인 수업자료를 찾을 수 없습니다."));

        lessonMaterial.setUserId(null); // 유저 Id를 없애서 매칭 안되도록 함
    }


    /**
     * ===============================================================================
     *                              검증 및 JsonNode 변환
     * ===============================================================================
     */


    /**
     * AI 반환 Json 파싱 메서드
     */
    private LessonMaterialResponseDto parseLessonMaterialJson(
            Long userId, String jsonResponse, String bookTitle, String author) {

        JsonNode jsonNode = null; // json 객체를 노드(element)로 사용한다.

        try {
            jsonNode = objectMapper.readTree(jsonResponse);

            // LessonMaterial 객체 생성
            LessonMaterial lessonMaterial = new LessonMaterial(
                    userId, // 제작자
                    bookTitle, // pdf 제목
                    jsonNode.path("pdftext").asText(""), // pdf 내용
                    author // 저자
            );

            // 역할 생성 및 추가
            List<LessonRole> roles = new ArrayList<>();
            JsonNode charactersNode = jsonNode.path("characters");
            if (!charactersNode.isMissingNode()) { // 노드가 존재할 때만 진행
                charactersNode.forEach(characterNode -> {
                    roles.add(new LessonRole(lessonMaterial, characterNode.asText("")));
                });
            }
            lessonMaterial.setLessonRoleList(roles);
            lessonMaterialRepository.save(lessonMaterial); // LessonMaterial 저장 (열린 질문, 퀴즈는 저장하지 않음)

            // 수업 자료 저장 아래는 클라이언트로 보내는 코드

            log.info("생성된 lessonMaterialId : {}", lessonMaterial.getLessonMaterialId());

            // 열린 질문을 OpenQuestionDto로 변환하여 클라이언트에 보낼 준비
            List<OpenQuestionResponseDto> openQuestionDtos = new ArrayList<>();

            JsonNode openQuestionsNode = jsonNode.path("open_questions"); //  라는 JSON에서 open_questions 노드 찾기

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
            JsonNode quizNode = jsonNode.path("quiz");
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

            // 저장된 역할을 RoleResponseDto로 변환
            List<RoleResponseDto> roleDtos = lessonMaterial.getLessonRoleList().stream()
                    .map(role -> new RoleResponseDto(
                            role.getRoleId(),
                            role.getRoleName()
                    ))
                    .collect(Collectors.toList());

            // 클라이언트에 반환할 LessonMaterialResponseDto 생성
            return new LessonMaterialResponseDto(
                    lessonMaterial.getLessonMaterialId(),
                    lessonMaterial.getBookContents(),
                    roleDtos,
                    quizDtos,
                    openQuestionDtos);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("json 변형이 잘못되었습니다.");
        }
    }

}