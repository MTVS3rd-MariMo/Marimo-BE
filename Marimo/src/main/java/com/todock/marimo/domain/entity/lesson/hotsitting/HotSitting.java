package com.todock.marimo.domain.entity.lesson.hotsitting;

import com.todock.marimo.domain.entity.lesson.Lesson;
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
@Table(name="hot_sitting")
public class HotSitting {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long hotSittingId; // 핫시팅 id

    // 수업은 하나의 핫시팅를 가진다.
    @OneToOne
    private Lesson lesson;

    // 핫시팅은 여러개의 자기소개를 가진다.
    @OneToMany(mappedBy = "hotSitting")
    private List<SelfIntroduction> selfIntroductionList = new ArrayList<>();

    // 음성파일 요약
    @Column(nullable = false)
    private String summation;
}