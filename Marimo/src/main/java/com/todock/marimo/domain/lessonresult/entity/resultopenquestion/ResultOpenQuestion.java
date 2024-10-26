package com.todock.marimo.domain.lessonresult.entity.resultopenquestion;

import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_result_open_question")
public class ResultOpenQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 열린 질문 id
    private Long openQuestionId;

    @Column(name = "open_question_title") // 열린 질문 제목
    private String questionTitle;

    @ManyToOne
    @JoinColumn(name = "lesson_result_id") // 수업 결과
    private LessonResult lessonResult;

    @OneToMany(mappedBy = "resultOpenQuestion")
    private List<ResultOpenQuestionAnswer> answers = new ArrayList<>();
}