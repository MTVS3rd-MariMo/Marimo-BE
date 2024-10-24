package com.todock.marimo.domain.lessonmaterial.entity;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.SelectedQuiz;
import com.todock.marimo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tbl_lesson_material")
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long lesson_material_id;
    
    // 유저는 여러개의 수업 준비를 가질 수 있음
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // 수업은 여러개의 수업자로를 선택할 수 있다.
    @ManyToOne
    @JoinColumn(name="lesson_id")
    private Lesson lesson;
    
    // 수업 준비는 여러개의 퀴즈를 가진다.
    @OneToMany(mappedBy = "lessonMaterial")
    private List<SelectedQuiz> selectedQuizList = new ArrayList<>();

    // 수업 준비는 여러개의 열린 질문을 가진다.
    @OneToMany(mappedBy = "lessonMaterial")
    private List<OpenQuestion> openQuestionList = new ArrayList<>();
}
