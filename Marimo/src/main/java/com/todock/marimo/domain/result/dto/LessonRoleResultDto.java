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

    public LessonRoleResultDto(String character) {
        this.character = character;
    }
}
