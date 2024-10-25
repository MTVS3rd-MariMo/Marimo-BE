package com.todock.marimo.domain.entity.lessonmaterial;

import com.todock.marimo.domain.entity.lessonmaterial.openquestion.OpenQuestion;
import com.todock.marimo.domain.entity.lessonmaterial.quiz.SelectedQuiz;
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
@Table(name = "tbl_lesson_material")
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonMaterialId; // 수업 자료 id

    @Column(name = "book_title") // 책 제목
    private String bookTitle;

    @Column(name = "book_contents") // 책 내용
    private String bookContents;

    // 열린질문
    @OneToMany
    private List<OpenQuestion> openQuestionList = new ArrayList<>();

    // 선택된 퀴즈
    @OneToMany
    private List<SelectedQuiz> selectedQuizList = new ArrayList<>();

    // 역할
    @OneToMany
    private List<Role> roleList = new ArrayList<>();

}
