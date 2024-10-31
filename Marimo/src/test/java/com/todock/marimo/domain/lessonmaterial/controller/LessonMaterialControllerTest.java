package com.todock.marimo.domain.lessonmaterial.controller;

import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialRegistRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.LessonRoleRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.OpenQuestionRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.QuizRequestDto;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.service.LessonMaterialService;
import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LessonMaterialControllerTest {

    @Autowired
    private LessonMaterialService lessonMaterialService;

    @Autowired
    private UserRepository userRepository;


    @Test
    void sendPdfToAiServer() {
    }

    @Test
    @Transactional
    void createLessonMaterial() {

        // Given
        User teacher = new User();
        teacher.setRole(Role.TEACHER);
        User savedTeacher = userRepository.save(teacher);

        LessonMaterialRegistRequestDto requestDto = new LessonMaterialRegistRequestDto();
        requestDto.setUserId(savedTeacher.getUserId());
        requestDto.setBookTitle("아기 돼지 삼형제");
        requestDto.setBookContents("옛날 옛적에 아기 돼지 삼형제가 살았습니다...");

        // OpenQuestionRequest 타입으로 변경
        List<OpenQuestionRequestDto> openQuestions = new ArrayList<>();
        openQuestions.add(new OpenQuestionRequestDto("아기 돼지들은 왜 집을 떠났을까요?"));
        openQuestions.add(new OpenQuestionRequestDto("늑대는 왜 아기 돼지들을 잡아먹으려고 했을까요?"));
        openQuestions.add(new OpenQuestionRequestDto("막내 돼지는 어떻게 형들과 자신을 지킬 수 있었나요?"));
        requestDto.setOpenQuestionList(openQuestions);

        // 퀴즈 2개 생성
        List<QuizRequestDto> quizzes = new ArrayList<>();
        quizzes.add(new QuizRequestDto(
                "첫째 돼지는 무엇으로 집을 지었나요?",
                "짚",
                "짚",
                "나무",
                "벽돌",
                "철"
        ));
        quizzes.add(new QuizRequestDto(
                "늑대는 어떻게 첫째와 둘째 돼지의 집을 무너뜨렸나요?",
                "입으로 후후 불어서",
                "도끼로 부숴서",
                "입으로 후후 불어서",
                "망치로 때려서",
                "어깨로 밀어서"
        ));
        requestDto.setQuizzeList(quizzes);

        // 역할 4개 생성
        List<LessonRoleRequestDto> roles = new ArrayList<>();
        roles.add(new LessonRoleRequestDto("첫째 돼지"));
        roles.add(new LessonRoleRequestDto("둘째 돼지"));
        roles.add(new LessonRoleRequestDto("막내 돼지"));
        roles.add(new LessonRoleRequestDto("늑대"));
        requestDto.setRoleList(roles);

        // When
        LessonMaterial savedMaterial = lessonMaterialService.save(requestDto);

        // Then
        assertNotNull(savedMaterial);
        assertEquals("아기 돼지 삼형제", savedMaterial.getBookTitle());
        assertEquals(3, savedMaterial.getOpenQuestionList().size());
        assertEquals(1, savedMaterial.getSelectedQuizList().size());
        assertEquals(2, savedMaterial.getSelectedQuizList().get(0).getQuizList().size());
        assertEquals(4, savedMaterial.getLessonRoleList().size());
    }


}