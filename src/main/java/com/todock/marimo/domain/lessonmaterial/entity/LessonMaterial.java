package com.todock.marimo.domain.lessonmaterial.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson_material", indexes = {
        @Index(name = "idx_lesson_material_id", columnList = "lesson_material_id")
})
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonMaterialId; // 수업 자료 id

    @Setter
    @Column(name = "user_id") // 만든 선생님 id
    private Long userId;

    @Column(name = "book_title") // 책 제목
    private String bookTitle;

    @Column(name = "author") // 저자
    private String author;

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

    @Setter
    @Column(name = "background_url") // 단체사진 배경
    private String backgroundUrl;

    @Column(name = "created_at")
    private String createdAt; // 생성 날짜


    public LessonMaterial(Long userId, String bookTitle, String bookContents, String author) {
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.userId = userId;
        this.author = author;

        // 현재 날짜와 시간을 포맷팅하여 문자열로 저장
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        this.createdAt = LocalDateTime.now().format(formatter);

    }

    public LessonMaterial(Long teacherId, String bookTitle, String bookContents,
                          List<OpenQuestion> openQuestionList,
                          List<Quiz> quizList) {
        this.userId = teacherId;
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.openQuestionList = openQuestionList;
        this.quizList = quizList;

        // 현재 날짜와 시간을 포맷팅하여 문자열로 저장
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분");
        this.createdAt = LocalDateTime.now().format(formatter);

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