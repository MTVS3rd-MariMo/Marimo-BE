package com.todock.marimo.domain.lesson.dto;

import com.todock.marimo.domain.lessonmaterial.dto.OpenQuestionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonOpenQuestionRequestDto {

    private Long lessonId;

    private List<OpenQuestionDto> openQuestionList;


}
