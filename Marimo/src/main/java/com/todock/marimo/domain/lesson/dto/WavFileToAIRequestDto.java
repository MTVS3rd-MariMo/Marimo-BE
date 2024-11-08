package com.todock.marimo.domain.lesson.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WavFileToAIRequestDto {

    private Long selfIntroductionId; // 기본값

    private Long lessonId;

    private String name;

    private String character;

    private Long selfIntNum;

    private MultipartFile wavFile;

}
