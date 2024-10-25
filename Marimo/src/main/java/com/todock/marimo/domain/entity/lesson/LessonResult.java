package com.todock.marimo.domain.entity.lesson;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class LessonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonResultId; // 수업 결과 id

    @OneToOne(mappedBy = "lesson") // 수업은 하나의 수업 결과를 가진다.
    private Lesson lesson;

    @Column(name = "open_question_result")
    private String openQuestionResult; // 열린 질문 결과

    @Column(name = "hot_sitting_summation")
    private String hotSittingSummation; // 핫시팅 요약

    @Column(name = "photo")
    private String photo; // 사진

    // 수업은 여러개의 참가자를 가진다.
    @OneToMany(mappedBy = "lessonResult")
    private List<Participant> participantList = new ArrayList<>();
}
