package com.todock.marimo.domain.lesson.entity.avatar;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long avatar_id;

    // 수업은 여러개의 아바타를 가진다.
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    // 유저는 역할에 맞게 가져야하는데 수정 필요
//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User user;

    @Column(name = "avatar_img")
    private String avatarImg;

    // 한 아바타에 애니메이션 두개
    @OneToMany(mappedBy = "avatar")
    private List<Animation> animationList = new ArrayList<>();
}
