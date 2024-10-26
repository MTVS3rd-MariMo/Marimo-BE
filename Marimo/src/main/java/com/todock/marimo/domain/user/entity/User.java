package com.todock.marimo.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 유저 id

    @Column(name = "role") // STUDENT, TEACHER
    private Role role;
    
    @Column(name = "school") // 학교
    private String school;

    @Column(name = "grade") // 학년
    private Integer grade;

    @Column(name = "class_room") // 반
    private Integer classRoom;

    // 선생님은 번호 없음
    @Column(name = "student_number") // 출석 번혼
    private Integer studentNumber;

    @Column(name = "name") // 이름 == 아이디
    private String name;

    @Column(name = "password") // 비밀번호
    private String password;

}
