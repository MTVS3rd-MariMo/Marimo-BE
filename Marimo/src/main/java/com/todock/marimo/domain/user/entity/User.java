package com.todock.marimo.domain.user.entity;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
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
@Table(name = "tbl_user")
public class User {

    @Id
    private Long user_id;

    // STUDENT, TEACHER
    @Column(name = "role")
    private Role role;

    @Column(name = "school")
    private String school;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "class_room")
    private Integer classRoom;

    // 선생님은 번호 없음
    @Column(name = "student_number")
    private Integer studentNumber;

    // 아이디 == 이름
    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    // 유저는 여러개의 수업을 가진다.
    @OneToMany(mappedBy = "user")
    private List<Lesson> lessonList = new ArrayList<>();

    // 유저는 여러개의 수업 준비를 가진다.
    @OneToMany(mappedBy = "user")
    private List<LessonMaterial> lessonMaterialList = new ArrayList<>();

}
