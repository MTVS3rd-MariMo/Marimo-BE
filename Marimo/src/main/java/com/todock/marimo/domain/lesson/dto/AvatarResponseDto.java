package com.todock.marimo.domain.lesson.dto;

import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponseDto {

    private Long userId;

    private String avatarImg;

    private List<Animation> animationList = new ArrayList<>();
}
