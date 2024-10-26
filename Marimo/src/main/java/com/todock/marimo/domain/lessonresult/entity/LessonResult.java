package com.todock.marimo.domain.lessonresult.entity;

import com.todock.marimo.domain.lessonresult.entity.resulthotsitting.ResultHotSitting;
import com.todock.marimo.domain.lessonresult.entity.resultopenquestion.ResultOpenQuestion;
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
public class LessonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 수업 결과 id
    private Long lessonResultId;

    @Column(name = "lesson_id") // 수업 id
    private Long lessonId;

    @ElementCollection
    @CollectionTable(name = "lesson_result_participants")
    private List<ResultParticipant> participantList = new ArrayList<>(); // 참가자, 역할

    @ElementCollection
    @CollectionTable(name = "lesson_result_avatars")
    private List<ResultAvatar> avatarList = new ArrayList<>(); // 아바타

    @OneToMany(mappedBy = "lessonResult")
    private List<ResultOpenQuestion> resultOpenQuestionList = new ArrayList<>(); // 열린 질문

    @OneToOne(mappedBy = "lessonResult")
    private ResultHotSitting hotSitting; // 핫시팅

    @ElementCollection
    @CollectionTable(name = "result_quiz")
    private List<ResultQuiz> quizList = new ArrayList<>(); // 퀴즈

    @Column(name = "photo") // 단체 사진
    private String photo;
}