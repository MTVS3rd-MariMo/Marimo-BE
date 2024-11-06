package com.todock.marimo.domain.lessonresult.entity.resulthotsitting;

import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_result_self_introduction")
public class ResultIntroduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotSittingId;

    @OneToOne
    @JoinColumn(name = "lesson_result_id", nullable = false) // 수업 결과와의 연관 관계
    private LessonResult lessonResult;

    // 참가자 자기소개 리스트
    @OneToMany(mappedBy = "hotSitting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantIntroduction> participantIntroductions = new ArrayList<>();

    // AI 서버에서 요약된 전체 텍스트
    @Column(name = "summary_text", columnDefinition = "TEXT")
    private String summaryText;
}