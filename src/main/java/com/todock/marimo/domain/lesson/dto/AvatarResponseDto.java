package com.todock.marimo.domain.lesson.dto;

import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResponseDto {

    @NotNull(message = "유저 ID는 필수 입력값입니다.")
    private Long userId; // 유저 ID

    @NotBlank(message = "아바타 이미지는 필수 입력값입니다.")
    private String avatarImg; // 아바타 이미지 URL

    @NotEmpty(message = "애니메이션 리스트는 최소 1개 이상이어야 합니다.")
    private List<Animation> animations = new ArrayList<>(); // 애니메이션 리스트

}
