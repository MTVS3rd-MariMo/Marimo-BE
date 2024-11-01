package com.todock.marimo.domain.lesson.entity.avatar;

import com.todock.marimo.domain.lesson.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long avatarId;

    @ManyToOne
    @JoinColumn(name="lessonId") // 수업 id
    private Lesson lesson;

    @Column(name = "user_id") // 유저 id
    private Long userId;

    @Column(name = "avatar_img") // 아바타 이미지
    private String avatarImg;

    // 한 아바타에 애니메이션 두개
    @OneToMany(mappedBy ="avatar")
    private List<Animation> animationList = new ArrayList<>();

    public Avatar(Long userId, String avatarImg, List<Animation> animationList) {
        this.userId = userId;
        this.avatarImg = avatarImg;
        this.animationList = animationList;
    }
}
