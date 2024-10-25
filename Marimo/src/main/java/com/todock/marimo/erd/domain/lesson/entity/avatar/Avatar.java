//package com.todock.marimo.erd.domain.lesson.entity.avatar;
//
//import com.todock.marimo.erd.domain.lesson.entity.Lesson;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name = "tbl_avatar")
//public class Avatar {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long avatar_id;
//
//    @ManyToOne
//    @JoinColumn(name="lesson_id")
//    private Lesson lesson;
//
//    @Column(name = "avatar_img")
//    private String avatarImg;
//
//    // 한 아바타에 애니메이션 두개
//    @OneToMany(mappedBy ="avatar")
//    private List<Animation> animationList = new ArrayList<>();
//}
