package com.todock.marimo.domain.lessonmaterial.dto;

import com.todock.marimo.domain.lessonmaterial.dto.reponse.RoleResponseDto;
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

    private String bookContents;

    private List<RoleResponseDto> roleList = new ArrayList<>();

    private List<OpenQuestionUpdateDto> openQuestionList = new ArrayList<>();

    private List<QuizDto> quizList = new ArrayList<>();

    }