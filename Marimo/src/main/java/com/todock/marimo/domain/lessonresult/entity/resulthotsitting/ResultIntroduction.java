package com.todock.marimo.domain.lessonresult.entity.resulthotsitting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_result_introduction")
public class ResultIntroduction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long introductionId;

    @ManyToOne
    @JoinColumn(name="result_hot_sitting_id")
    private ResultHotSitting resultHotSitting;

    @Column(name="user_id")
    private Long userId;

    @Column(name="contents")
    private String contents;
}