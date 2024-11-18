package com.todock.marimo.domain.lesson.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BackgroundRequestDto {

    private String pdf_text;

    private Long lessonId;
}
