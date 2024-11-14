package com.todock.marimo.domain.user.dto;

import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserRequestDto {

    private String name;
    private String password;

}
