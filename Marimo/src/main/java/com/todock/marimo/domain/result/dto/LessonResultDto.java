package com.todock.marimo.domain.result.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonResultDto {

    // 책 제목
    private String bookTitle;

    // 수업 생성 시간
    private String createdAt;

    // 참가자
    private List<String> participants = new ArrayList<>();

    // 역할
    private List<LessonRoleResultDto> Roles = new ArrayList<>();

    // 열린 질문
    private List<OpenQuestionResultDto> openQuestions = new ArrayList<>();

    // 핫시팅
    private List<HotSittingResultDto> hotSittings = new ArrayList<>();

    // 사진
    private String photoUrl;

    public LessonResultDto(String bookTitle, String createdAt, String photoUrl) {
        this.bookTitle = bookTitle;
        this.createdAt = createdAt;
        this.photoUrl = photoUrl;
    }
}
