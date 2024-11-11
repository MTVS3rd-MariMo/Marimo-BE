package com.todock.marimo.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LessonRoleResultDto {

    private String userName;

    private String character;

    private String avatarUrl;

    public LessonRoleResultDto(String character, String avatarUrl) {
        this.character = character;
        this.avatarUrl = avatarUrl;
    }
}
