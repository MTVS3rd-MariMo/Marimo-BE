package com.todock.marimo.domain.lessonresult.entity.resulthotsitting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "question_answer")
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long questionAnswerId;

    private Long introduceId;  // SelfIntroduction과의 관계 설정

    private String content;  // 예시에서 수연[도원]의 발언 내용
}