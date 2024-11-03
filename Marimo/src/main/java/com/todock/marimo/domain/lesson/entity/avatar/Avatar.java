package com.todock.marimo.domain.lesson.entity.avatar;

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
    @JoinColumn(name = "lessonId") // 수업 id
    private Lesson lesson;

    @Column(name = "user_id") // 유저 id
    private Long userId;

    @Column(name = "avatar_img") // 아바타 이미지
    private String avatarImg;

    // 한 아바타에 애니메이션 두개
    @JsonManagedReference
    @OneToMany(mappedBy = "avatar", cascade = CascadeType.ALL)
    private List<Animation> animations = new ArrayList<>();

    public Avatar(Long userId, String avatarImg, List<Animation> animations) {
        this.userId = userId;
        this.avatarImg = avatarImg;
        this.animations = animations;
    }

    @Override
    public String toString() {
        return "Avatar{" +
                "animations=" + animations +
                ", avatarImg='" + avatarImg + '\'' +
                ", userId=" + userId +
                ", lesson=" + lesson +
                ", avatarId=" + avatarId +
                '}';
    }
}
