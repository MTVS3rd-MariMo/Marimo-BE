package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenQuestionDto {

    private Long OpenQuestionId;

    private String question;

    private List<OpenQuestionAnswerDto> openQuestionAnswerList;
}
