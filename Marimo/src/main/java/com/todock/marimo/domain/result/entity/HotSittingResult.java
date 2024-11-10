package com.todock.marimo.domain.result.entity;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HotSittingResult {

    private Long userId;

    private String userName; // 자기소개 한 사람 이름

    private String selfIntroduce;

    private List<String> questionAnswers; // 자기소개에 대한 질의응답
}
