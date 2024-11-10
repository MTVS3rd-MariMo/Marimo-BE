package com.todock.marimo.domain.result.dto;

import com.todock.marimo.domain.lessonmaterial.dto.QuizDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDto {

    private QuizDto quiz;

}
