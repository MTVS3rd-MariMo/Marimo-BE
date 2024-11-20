package com.todock.marimo.domain.lesson.entity.hotsitting;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @JsonBackReference
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @JsonManagedReference
    @OneToMany(mappedBy = "hotSitting", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SelfIntroduce> selfIntroduces = new ArrayList<>();

    public HotSitting(List<SelfIntroduce> selfIntroduces) {
        this.selfIntroduces = selfIntroduces;
    }
}