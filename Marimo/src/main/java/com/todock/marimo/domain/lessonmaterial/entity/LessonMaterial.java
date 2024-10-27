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

    @OneToMany(mappedBy = "lessonMaterial",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OpenQuestion> openQuestionList = new ArrayList<>();

    @OneToMany(mappedBy = "lessonMaterial",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<SelectedQuiz> selectedQuizList = new ArrayList<>();

    @OneToMany(mappedBy = "lessonMaterial",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Role> roleList = new ArrayList<>();

    // 생성자
    public LessonMaterial(String bookTitle, String bookContents) {

        validateBookInfo(bookTitle, bookContents);
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
    }

    // 열린 질문 추가
    public void addOpenQuestion(OpenQuestion openQuestion) {
        validateOpenQuestionCount();
        this.openQuestionList.add(openQuestion);
        openQuestion.setLessonMaterial(this);
    }

    // 퀴즈 추가
    public void addSelectedQuiz(SelectedQuiz selectedQuiz) {
        validateSelectedQuizCount();
        this.selectedQuizList.add(selectedQuiz);
        selectedQuiz.setLessonMaterial(this);
    }

    // 역할 추가
    public void addRole(Role role) {
        validateRoleCount();
        this.roleList.add(role);
    }


    // 책 내용 검증
    private void validateBookInfo(String title, String contents) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("책 제목이 없습니다.");
        }
        if (contents == null || contents.trim().isEmpty()) {
            throw new IllegalArgumentException("책 내용이 없습니다.");
        }
    }

    private void validateOpenQuestionCount() {
        if (openQuestionList.size() != 3) {
            throw new IllegalArgumentException("열린 질문은 무조건 3개를 등록해야 합니다.");
        }
    }

    private void validateSelectedQuizCount() {
        if (selectedQuizList.size() != 2) {
            throw new IllegalArgumentException("퀴즈는 무조건 2개를 등록해야 합니다.");
        }
    }

    private void validateRoleCount() {
        if (roleList.size() != 4) {
            throw new IllegalArgumentException("역할은 무조건 4개를 등록해야 합니다.");
        }
    }
}