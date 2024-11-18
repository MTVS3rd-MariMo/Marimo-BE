package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.OpenQuestionResponseDto;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DetailLessonMaterialDto {

    private Long lessonMaterialId;

    private List<OpenQuestionUpdateDto> openQuestionList= new ArrayList<>();

    private List<QuizDto> quizList= new ArrayList<>();

}
