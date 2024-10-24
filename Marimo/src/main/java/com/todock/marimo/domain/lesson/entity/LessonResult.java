package com.todock.marimo.domain.lesson.entity;

import com.todock.marimo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson_result")
public class LessonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lesson_result_id;

    //
    @OneToOne
    @JoinColumn(name = "lesson_id") // 수업에 대한 외래 키
    private Lesson lesson;

    // 수업결과가 여러명의 유저를 가지고 있다.
    @ManyToOne
    @JoinColumn(name = "user_id") // 결과를 만든 유저에 대한 외래 키
    private User user;

    @Column(name = "result_type")
    private String resultType; // 예: '퀴즈', '열린 질문', '아바타 만들기', '사진 찍기' 등

    @Column(name = "result_content")
    private String resultContent; // 결과 내용 (예: 답변, 사진 URL 등)

    @Column(name = "timestamp")
    private Long timestamp; // 활동 시간 (optional)

}
