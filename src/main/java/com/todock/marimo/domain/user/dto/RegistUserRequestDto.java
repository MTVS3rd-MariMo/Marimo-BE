package com.todock.marimo.domain.user.dto;

import com.todock.marimo.domain.user.entity.Role;
import lombok.*;

/**
 *  유저 생성 request dto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistUserRequestDto {

    private Role role; // 선생님 / 학생

    private String school; // 학교

    private Integer grade; // 학년

    private Integer classRoom; // 반

    // 선생님은 번호 없음
    private Integer studentNumber; // 출석 번호

    private String name; // 이름 == 아이디

    private String password;



}
