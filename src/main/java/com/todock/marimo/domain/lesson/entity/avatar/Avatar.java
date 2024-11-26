package com.todock.marimo.domain.lesson.entity.avatar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lesson.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_avatar")
public class Avatar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long avatarId;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "lesson_id") // 수업 id
    private Lesson lesson;

    @Column(name = "user_id") // 유저 id
    private Long userId;

    @Column(name = "avatar_img") // 아바타 이미지
    private String avatarImg;

    @Column(name = "charcter") // 아바타의 역할
    private String character;

    // 한 아바타에 애니메이션 두개
    @JsonManagedReference
    @OneToMany(mappedBy = "avatar", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Animation> animations = new ArrayList<>();

    public Avatar(Lesson lesson, Long userId, String avatarImg, List<Animation> animations) {
        this.userId = userId;
        this.avatarImg = avatarImg;
        this.animations = animations;
    }

    public Avatar(Lesson lesson, Long userId, String avatarImg, String character, List<Animation> animations) {
        this.lesson = lesson;
        this.userId = userId;
        this.avatarImg = avatarImg;
        this.character = character;
        this.animations = animations;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "animations=" + animations +
                ", avatarImg='" + avatarImg + '\'' +
                ", userId=" + userId +
                ", avatarId=" + avatarId +
                '}';
    }
}
