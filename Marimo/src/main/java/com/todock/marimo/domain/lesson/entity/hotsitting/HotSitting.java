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
@Table(name = "tbl_hot_sitting")
public class HotSitting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotSittingId; // 핫시팅 id

    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @OneToMany(mappedBy = "hotSitting", cascade = CascadeType.ALL)
    private List<SelfIntroduce> selfIntroduces = new ArrayList<>();

//    public HotSitting(Lesson lesson) {
//        this.lesson = lesson;
//    }
}