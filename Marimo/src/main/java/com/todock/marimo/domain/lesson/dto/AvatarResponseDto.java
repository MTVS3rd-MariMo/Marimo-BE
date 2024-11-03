package com.todock.marimo.domain.lesson.dto;

import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AvatarResponseDto {

    private Long userId;

    private String avatarImg;

    private List<Animation> animations = new ArrayList<>();

    public AvatarResponseDto(Long userId, String avatarImg, List<Animation> animations) {
        this.userId = userId;
        this.avatarImg = avatarImg;
        this.animations = animations;
    }
}
