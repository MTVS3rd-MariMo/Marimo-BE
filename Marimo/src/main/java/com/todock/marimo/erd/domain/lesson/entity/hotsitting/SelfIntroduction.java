//package com.todock.marimo.erd.domain.lesson.entity.hotsitting;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "tbl_self_introduce")
//public class SelfIntroduction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long self_introduce_id;
//
//    // 핫시팅은 여러개의 자기소개를 가진다.
//    @ManyToOne
//    @JoinColumn(name = "hot_sitting_id")
//    private HotSitting hotSitting;
//
//    @Column(name = "contents")
//    private String contents;
//}
