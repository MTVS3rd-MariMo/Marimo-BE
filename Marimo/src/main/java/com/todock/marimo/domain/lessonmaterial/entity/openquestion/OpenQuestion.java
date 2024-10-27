package com.todock.marimo.domain.lessonmaterial.entity.openquestion;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@Table(name = "tbl_open_question")
public class OpenQuestion {

    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openQuestionId; // 열린 질문 id

    @ManyToOne
    @JoinColumn(name = "lesson_material_id", nullable = false) // 수업 자료
    private LessonMaterial lessonMaterial;

    @Column(name = "open_question_title", nullable = false) // 열린 질문(내용)
    private String questionTitle;

    @OneToMany(mappedBy = "openQuestion", // 열린 질문 답변 
            cascade = CascadeType.ALL, // 질문 삭제시 답변도 삭제
            orphanRemoval = true) // 고아 객체 자동으로 삭제
    private List<OpenQuestionAnswer> openQuestionAnswerList;

    // 생성자
    public OpenQuestion(LessonMaterial lessonMaterial, String questionTitle) {

        validateOpenQuestionTitle(questionTitle); // 답변 검증

        this.lessonMaterial = lessonMaterial;
        this.questionTitle = questionTitle;
        this.openQuestionAnswerList = new ArrayList<>();
    }

    // 답변 추가 메서드
    public void addAnswer(Long userId, String answer) {

        // 유효성 검증
        validateOpenQuestionUserId(userId);
        // 답변 객체 생성
        OpenQuestionAnswer openQuestionAnswer = new OpenQuestionAnswer(userId, answer);

        validateOpenQuestionAnswer(openQuestionAnswer); // 답변 객체 생성 확인

        openQuestionAnswerList.add(openQuestionAnswer); // 리스트에 추가
        openQuestionAnswer.setOpenQuestion(this); // 적용 후 openQuestionAnswer에 적용하기
    }

    // 동화 제목 검증
    private void validateOpenQuestionTitle(String questionTitle) {
        if (questionTitle == null || questionTitle.isEmpty()) {
            throw new IllegalArgumentException("질문이 없습니다.");
        }
    }

    // 질문 제목 검증
    private void validateOpenQuestionUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("유저 ID가 없습니다.");
        }
    }

    // 답변 검증
    private void validateOpenQuestionAnswer(OpenQuestionAnswer openQuestionAnswer) {
        if (openQuestionAnswer == null) {
            throw new IllegalArgumentException("답변은 null일 수 없습니다.");
        }
    }

    // LessonMaterial 설정 메서드
    public void setLessonMaterial(LessonMaterial lessonMaterial) {
        this.lessonMaterial = lessonMaterial;
    }

}
