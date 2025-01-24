package com.todock.marimo.domain.result.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LessonResultDto {

    // 책 제목
    private String bookTitle;

    // 수업 생성 시간
    private String createdAt;

    // 역할
    private List<LessonRoleResultDto> Roles = new ArrayList<>();

    // 열린 질문
    private List<OpenQuestionResultDto> openQuestions = new ArrayList<>();

    // 핫시팅
    private List<HotSittingResultDto> hotSittings = new ArrayList<>();

    public LessonResultDto(String bookTitle, String createdAt) {
        this.bookTitle = bookTitle;
        this.createdAt = createdAt;
    }
}
