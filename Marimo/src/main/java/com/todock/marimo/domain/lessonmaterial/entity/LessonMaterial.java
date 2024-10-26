package com.todock.marimo.domain.lessonmaterial.entity;

import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.SelectedQuiz;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson_material")
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonMaterialId; // 수업 자료 id

    @Column(name = "book_title") // 책 제목
    private String bookTitle;

    @Column(name = "book_contents") // 책 내용
    private String bookContents;


    @OneToMany(mappedBy = "lessonMaterial") // 열린질문
    private List<OpenQuestion> openQuestionList = new ArrayList<>();


    @OneToMany(mappedBy = "lessonMaterial") // 선택된 퀴즈
    private List<SelectedQuiz> selectedQuizList = new ArrayList<>();


    @OneToMany(mappedBy = "lessonMaterial") // 역할
    private List<Role> roleList = new ArrayList<>();

}
