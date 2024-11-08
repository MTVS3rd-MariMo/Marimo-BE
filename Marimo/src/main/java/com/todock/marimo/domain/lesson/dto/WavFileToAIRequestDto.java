package com.todock.marimo.domain.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WavFileToAIRequestDto {

    private Long selfIntroductionId = null; // 기본값

    private Long lessonId;

    private String userName;

    private String character;

    private Long selfIntNum;

    private MultipartFile wavFile;

}
