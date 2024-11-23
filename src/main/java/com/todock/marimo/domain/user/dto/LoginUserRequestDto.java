package com.todock.marimo.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequestDto {

    @NotBlank(message = "이름(아이디)는 필수 입력값입니다.")
    private String name;

    @NotBlank(message = "이름(아이디)는 필수 입력값입니다.")
    private String password;

}
