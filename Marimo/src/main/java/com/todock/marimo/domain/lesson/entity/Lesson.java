package com.todock.marimo.domain.lesson.entity;


import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_lesson")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    private Long lessonId; // 수업 id

    @Column(name = "lesson_material_id")
    private Long lessonMaterialId; // 사용한 수업 자료
    
    @OneToMany(mappedBy = "lesson")
    private List<Participant> participantList = new ArrayList<>(); // 참가자 목록

    @OneToMany(mappedBy = "lesson")
    private List<Avatar> avatarList = new ArrayList<>(); // 아바타 목록

    @OneToOne(mappedBy = "lesson")
    private HotSitting hotSitting; // 핫시팅 활동

    @Column(name="photo_url")
    private String photoUrl; // 단체 사진

    public Lesson(Long lessonMaterialId) {
        this.lessonMaterialId = lessonMaterialId;
    }
}
