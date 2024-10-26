package com.todock.marimo.domain.lessonmaterial.entity.quiz;

import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_selected_quiz")
public class SelectedQuiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selectedQuizId;

    // 수업 준비는 여러개의 선택된 퀴즈을 가질 수 있다.
    @ManyToOne
    @JoinColumn(name = "lesson_material_id", nullable = false)
    private LessonMaterial lessonMaterial;

    @Column(name = "contents") // 보기 내용
    private String contents;

    @Column(name="first_choice") // 첫번째 보기
    private String firstChoice;

    @Column(name="second_choice") // 두번째 보기
    private String secondChoice;

    @Column(name="third_choice") // 세번째 보기
    private String thirdChoice;

    @Column(name="fourth_choice")// 네번째 보기
    private String fourthChoice;

}
