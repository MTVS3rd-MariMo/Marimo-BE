//package com.todock.marimo.domain.entity.lesson;
//
//import com.todock.marimo.domain.entity.lessonmaterial.quiz.SelectedQuiz;
//import jakarta.persistence.*;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//public class LessonResult {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long lessonResultId; // 수업 결과 id
//
//    @Column(name = "lesson_id")
//    private Long lessonId;
//
//    // 참여한 유저 id
//
//    // 사진
//    @Column(name = "photo")
//    private String photo;
//
//    // 열린 질문에 대한 대답
//
//    // 핫시팅 요약본
//    @Column(name = "hot_sitting_summation", columnDefinition = "TEXT")
//    private String hotSittingSummation;
//    // 퀴즈 문제와 정답
//    @OneToMany(mappedBy = "lessonResult")
//    private List<SelectedQuiz> SelectedQuizList = new ArrayList<>();
//}