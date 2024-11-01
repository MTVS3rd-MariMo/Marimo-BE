package com.todock.marimo.domain.lesson.entity;


import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
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
@Table(name = "tbl_lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lessonId; // 수업 id

    // 수업은 여러개의 참가자를 가진다.
    @OneToMany(mappedBy = "lesson")
    private List<Participant> participantList = new ArrayList<>();

    // 수업은 여러개의 아바타를 가진다.
    @OneToMany(mappedBy = "lesson")
    private List<Avatar> avatarList = new ArrayList<>();

    // 수업은 하나의 핫시팅을 가진다.
    @OneToOne(mappedBy = "lesson")
    private HotSitting hotSitting;

    // 수업은 하나의 단체사진을 가진다.
    @OneToOne(mappedBy = "lesson")
    private Photo photo;

}
