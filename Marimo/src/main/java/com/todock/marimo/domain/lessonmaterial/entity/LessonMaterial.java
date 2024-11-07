package com.todock.marimo.domain.lessonmaterial.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.quiz.Quiz;
import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson_material")
public class LessonMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonMaterialId; // 수업 자료 id

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

    @OneToMany(mappedBy = "lessonMaterial", // 역할
            cascade = CascadeType.ALL)
    private List<LessonRole> lessonRoleList = new ArrayList<>();

    // 생성자
    public LessonMaterial(Long userId, String bookTitle, String bookContents) {
        // validateTeacherId(userId);
        // validateBookInfo(bookTitle, bookContents);

        this.userId = userId;
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
    }

    public LessonMaterial(Long teacherId, String bookTitle, String bookContents,
                          List<OpenQuestion> openQuestionList,
                          List<Quiz> quizList,
                          List<LessonRole> lessonRoleList) {
        this.userId = teacherId;
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
        this.openQuestionList = openQuestionList;
        this.quizList = quizList;
        this.lessonRoleList = lessonRoleList;
    }



//    // 열린 질문 추가
//    public void addOpenQuestion(OpenQuestion openQuestion) {
//        validateOpenQuestionCount();
//        this.openQuestionList.add(openQuestion);
//        openQuestion.setLessonMaterial(this);
//    }
//
//    // 퀴즈 추가
//    public void addSelectedQuiz(Quiz quiz) {
//        //validateSelectedQuizCount();
//        this.quizList.add(quiz);
//    }
//
//    // 역할 추가
//    public void addRole(LessonRole lessonRole) {
//        //validateRoleCount();
//        this.lessonRoleList.add(lessonRole);
//    }
//
//    // 유저 권한 검증
//    private void validateTeacherId(Long teacherId) {
//        if (teacherId == null) {
//            throw new IllegalArgumentException("선생님의 id가 없습니다.");
//        }
//    }
//
//    // 책 내용 검증
//    private void validateBookInfo(String title, String contents) {
//        if (title == null || title.trim().isEmpty()) {
//            throw new IllegalArgumentException("책 제목이 없습니다.");
//        }
//        if (contents == null || contents.trim().isEmpty()) {
//            throw new IllegalArgumentException("책 내용이 없습니다.");
//        }
//    }

    // 열린 질문 수 검증
    private void validateOpenQuestionCount() {
        if (openQuestionList.size() != 3) {
            throw new IllegalArgumentException("열린 질문은 무조건 3개를 등록해야 합니다.");
        }
    }
//
//    // 퀴즈 수 검증
//    private void validateSelectedQuizCount() {
//        if (selectedQuizList.size() != 2) {
//            throw new IllegalArgumentException("퀴즈는 무조건 2개를 등록해야 합니다.");
//        }
//    }
//
//    // 역할 수 검증
//    private void validateRoleCount() {
//        if (lessonRoleList.size() != 4) {
//            throw new IllegalArgumentException("역할은 무조건 4개를 등록해야 합니다.");
//        }
//    }

    // 선생님 권한 검증
    private void validateTeacherRole(User user) {
        if (user == null) {
            throw new IllegalArgumentException("선생님 정보는 필수입니다.");
        }
        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("선생님만 수업 자료를 생성할 수 있습니다.");
        }
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