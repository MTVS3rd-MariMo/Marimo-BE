package com.todock.marimo.domain.user.dto;

import com.todock.marimo.domain.user.entity.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * 유저 생성 request dto
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUserRequestDto {

    @NotBlank(message = "이름(아이디)은 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

    @NotNull(message = "역할(Role)은 필수 입력값입니다.")
    private Role role;

    @NotBlank(message = "학교 정보는 필수 입력값입니다.")
    private String school;

    @NotNull(message = "학년은 1 이상의 값이어야 합니다.")
    @Min(value = 1, message = "학년은 1 이상의 값이어야 합니다.")
    private Integer grade;

    @NotNull(message = "반은 1 이상의 값이어야 합니다.")
    @Min(value = 1, message = "반은 1 이상의 값이어야 합니다.")
    private Integer classRoom;

    @Min(value = 1, message = "학생 번호는 1 이상의 값이어야 합니다.")
    private Integer studentNumber;

}
