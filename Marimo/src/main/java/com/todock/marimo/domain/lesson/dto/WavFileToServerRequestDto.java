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
public class WavFileToServerRequestDto {

    private Long lessonId;

    private String userName;

    private String character;

    private MultipartFile wavFile;
}
