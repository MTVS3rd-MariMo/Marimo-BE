package com.todock.marimo.domain.lessonresult.entity.resultopenquestion;

import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_result_open_question")
public class ResultOpenQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 열린 질문 id
    private Long openQuestionId;

    @ManyToOne
    @JoinColumn(name = "lesson_result_id") // 수업 결과
    private LessonResult lessonResult;

    @Column(name = "open_question") // 열린 질문 제목
    private String question;

    @OneToMany(mappedBy = "resultOpenQuestion")
    private List<ResultOpenQuestionAnswer> openQuestionAnswerList = new ArrayList<>();
}