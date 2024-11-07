package com.todock.marimo.domain.lesson.entity.hotsitting;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lessonresult.entity.resulthotsitting.QuestionAnswer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hot_sitting")
public class HotSitting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotSittingId; // 핫시팅 id

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "self_introduce")
    private String SelfIntroduce;

    @OneToMany(mappedBy = "hotSitting")
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();
}