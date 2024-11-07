package com.todock.marimo.domain.lesson.entity.hotsitting;

import com.todock.marimo.domain.lesson.entity.Lesson;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hot_sitting")
public class HotSitting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotSittingId; // 핫시팅 id

    @Column(name = "user_id") // 말한 사람
    private Long userId;

    @Column(name = "self_int_num")
    private Long selfIntNum;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Column(name = "self_introduce")
    private String selfIntroduce;

    @OneToMany(mappedBy = "hotSitting")
    private List<QuestionAnswer> questionAnswers;

    public HotSitting(Lesson lesson, Long userId, Long selfIntNum, String selfIntroduce) {
        this.lesson = lesson;
        this.userId = userId;
        this.selfIntNum = selfIntNum;
        this.selfIntroduce = selfIntroduce;
        questionAnswers = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "HotSitting{" +
                "selfIntroduce='" + selfIntroduce + '\'' +
                ", questionAnswers=" + questionAnswers +
                '}';
    }
}