package com.todock.marimo.domain.entity.lessonmaterial.quiz;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 한 퀴즈에서 사용할 선택지를 관리하는 엔티티
 */

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_choice")
public class Choice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long choiceId;

    // 퀴즈는 여러개의 보기를 가진다.
    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false) // quiz_id 외래 키를 설정
    private Quiz quiz; // quiz

    @Column(name = "contents") // 보기 내용
    private String contents;
    
}