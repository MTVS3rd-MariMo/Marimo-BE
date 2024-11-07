package com.todock.marimo.domain.lessonresult.entity.resulthotsitting;

import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
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
@Table(name = "tbl_SpeechToText")
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionAnswerId;

    @ManyToOne
    @JoinColumn(name = "hot_sitting_id")
    private HotSitting hotSitting; // 핫시팅에 연결된 질문-답변

    @Column(name = "contents")
    private String contents;
}