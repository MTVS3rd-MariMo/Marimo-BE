package com.todock.marimo.domain.result.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    @Column(name = "lesson_id", nullable = false)
    private Long lessonId;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

    @Lob
    @Column(name = "book_contents", nullable = false, columnDefinition = "LONGTEXT")
    private String bookContents;

    @ElementCollection
    @CollectionTable(name = "result_participant", joinColumns = @JoinColumn(name = "result_id"))
    private List<ParticipantResult> participants = new ArrayList<>();

    @OneToMany
    @CollectionTable(name = "result_open_question", joinColumns = @JoinColumn(name = "result_id"))
    private List<OpenQuestionResult> openQuestions = new ArrayList<>();

    @Embedded
    private HotSittingResult hotSitting;

    @ElementCollection
    @CollectionTable(name = "result_quiz", joinColumns = @JoinColumn(name = "result_id"))
    private List<QuizResult> quizzes = new ArrayList<>();

    private String photoUrl;

    public Result(Long lessonId, String bookTitle, String bookContents) {
        this.lessonId = lessonId;
        this.bookTitle = bookTitle;
        this.bookContents = bookContents;
    }
}
