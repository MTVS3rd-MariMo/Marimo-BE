package com.todock.marimo.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId; // 유저 id


    @Column(name = "role") // STUDENT, TEACHER
    @Enumerated(value = EnumType.STRING)
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

    // 생성자
    public User(Role role, String school, Integer grade, Integer classRoom,
                Integer studentNumber, String name, String password) {
        this.role = role;
        this.school = school;
        this.grade = grade;
        this.classRoom = classRoom;
        this.studentNumber = studentNumber;
        this.name = name;
        this.password = password;
    }
}
