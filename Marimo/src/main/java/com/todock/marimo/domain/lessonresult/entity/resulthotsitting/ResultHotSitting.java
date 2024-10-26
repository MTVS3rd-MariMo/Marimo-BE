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
    private Long hotSittingId;

    @OneToOne
    @JoinColumn(name = "lesson_result_id")
    private LessonResult lessonResult;

    @Column(name = "summary")
    private String summary;

    @OneToMany(mappedBy = "resultHotSitting")
    private List<ResultIntroduction> introductionList = new ArrayList<>();
}
