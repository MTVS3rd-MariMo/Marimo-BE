//package com.todock.marimo.erd.domain.lessonmaterial.entity.quiz;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
///**
// * 한 퀴즈에서 사용할 선택지를 관리하는 엔티티
// */
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "tbl_choice")
//public class Choice {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long choice_id;
//
//    // 퀴즈는 여러개의 보기를 가진다.
//    @ManyToOne
//    @JoinColumn(name = "quiz_id", nullable = false) // quiz_id 외래 키를 설정
//    private Quiz quiz; // quizId -> quiz로 이름 변경
//
//    @Column(name = "contents")
//    private String contents;
//
//    // 정답 알려주면 컬럼 필요없음
//    @Column(name = "is_correct")
//    private boolean isCorrect;
//}