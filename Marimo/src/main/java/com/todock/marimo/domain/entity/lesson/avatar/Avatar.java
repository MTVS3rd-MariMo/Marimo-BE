package com.todock.marimo.domain.entity.lesson.avatar;

import com.todock.marimo.domain.entity.lesson.Lesson;
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
    private Long avatarId;

    @ManyToOne
    @JoinColumn(name="lesson_id") // 아바타 id
    private Lesson lesson;

    @Column(name = "avatar_img") // 아바타 이미지
    private String avatarImg;

    // 한 아바타에 애니메이션 두개
    @OneToMany(mappedBy ="avatar")
    private List<Animation> animationList = new ArrayList<>();
}
