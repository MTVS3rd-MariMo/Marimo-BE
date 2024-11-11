package com.todock.marimo.domain.lesson.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Column(name="created_user_id")
    private Long createdUserId;

    @Column(name = "lesson_material_id")
    private Long lessonMaterialId; // 사용한 수업 자료

    @JsonManagedReference
    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<Participant> participantList = new ArrayList<>(); // 참가자 목록

    @JsonManagedReference
    @OneToMany(mappedBy = "lesson", fetch = FetchType.LAZY)
    private List<Avatar> avatarList = new ArrayList<>(); // 아바타 목록

    @OneToOne(mappedBy = "lesson")
    private HotSitting hotSitting; // 핫시팅 활동

    @Column(name = "photo_url")
    private String photoUrl; // 단체사진

    @Column(name = "photo_background_url")
    private String photoBackgroundUrl; // 단체사진 배경

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 수업 생성 날짜

    public Lesson(Long createdUserId, Long lessonMaterialId) {

        this.createdAt = LocalDateTime.now(); // 생성 시 현재 날짜 표시
        this.createdUserId = createdUserId;
        this.lessonMaterialId = lessonMaterialId;
        
    }

    public String getFormattedCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 hh시 mm분");
        return this.createdAt.format(formatter);
    }
}
