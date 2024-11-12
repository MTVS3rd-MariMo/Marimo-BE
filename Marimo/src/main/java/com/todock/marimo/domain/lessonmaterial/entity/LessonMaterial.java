package com.todock.marimo.domain.lessonmaterial.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson_material")
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonMaterialId; // 수업 자료 id

    @Setter
    @Column(name = "user_id", nullable = false) // 만든 선생님 id
    private Long userId;

    @Column(name = "book_title") // 책 제목
    private String bookTitle;

    @Lob
    @Column(name = "book_contents", columnDefinition = "LONGTEXT")
    private String bookContents;

    @JsonManagedReference
    @OneToMany(mappedBy = "lessonMaterial", // 열린 질문
            cascade = CascadeType.ALL)
    private List<OpenQuestion> openQuestionList = new ArrayList<>();

    @OneToMany(mappedBy = "lessonMaterial" // 퀴즈
            , cascade = CascadeType.ALL)
    private List<Quiz> quizList = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "lessonMaterial", // 역할
            cascade = CascadeType.ALL)
    private List<LessonRole> lessonRoleList = new ArrayList<>();

    @Column(name = "created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy년 MM월 dd일 hh시 mm분")
    private LocalDateTime createdAt; // 생성 날짜


    public LessonMaterial(Long userId, String bookTitle, String bookContents) {
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.lessonRoleList = lessonRoleList;
        this.userId = userId;
    }

    public LessonMaterial(Long teacherId, String bookTitle, String bookContents,
                          List<OpenQuestion> openQuestionList,
                          List<Quiz> quizList) {
        this.userId = teacherId;
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.openQuestionList = openQuestionList;
        this.quizList = quizList;
    }

    @Override
    public String toString() {
        return "LessonMaterial{" +
                "userId=" + userId +
                ", bookTitle='" + bookTitle + '\'' +
                ", bookContents='" + bookContents + '\'' +
                ", lessonMaterialId=" + lessonMaterialId +
                ", openQuestionList=" + openQuestionList +
                ", quizList=" + quizList +
                ", lessonRoleList=" + lessonRoleList +
                '}';
    }

}