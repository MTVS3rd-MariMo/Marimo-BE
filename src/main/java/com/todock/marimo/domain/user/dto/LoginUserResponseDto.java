package com.todock.marimo.domain.user.dto;

import com.todock.marimo.domain.user.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponseDto {

    @NotNull(message = "userId는 필수 입력값입니다.")
    private Long userId;

    @NotNull(message = "역할(Role)은 필수 입력값입니다.")
    private Role role;

}
