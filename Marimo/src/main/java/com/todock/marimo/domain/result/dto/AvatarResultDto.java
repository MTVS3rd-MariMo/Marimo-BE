package com.todock.marimo.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvatarResultDto {

    private String userName;

    private String lessonRole;

    private String avatar;

    public AvatarResultDto(String avatar) {
        this.avatar = avatar;
    }
}
