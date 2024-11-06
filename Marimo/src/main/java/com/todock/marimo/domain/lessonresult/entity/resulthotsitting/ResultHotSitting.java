package com.todock.marimo.domain.lessonresult.entity.resulthotsitting;

import com.todock.marimo.domain.lessonresult.entity.LessonResult;
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
@AllArgsConstructor
@Table(name = "tbl_result_hot_sitting")
public class ResultHotSitting {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long hotSittingId;

    private Long lessonId;  // Lesson과의 관계 설정

    private String SelfIntroduction;

    private Long introduceId;

    private List<QuestionAnswer> questionAnswers;  // 질문과 답변의 리스트
}
