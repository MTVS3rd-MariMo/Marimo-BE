package com.todock.marimo.domain.user.dto;

import com.todock.marimo.domain.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserResponseDto {

    private Long userId;

    private Role role;

    @Override
    public String toString() {
        return "LoginUserResponseDto{" +
                "userId=" + userId +
                ", role=" + role +
                '}';
    }
}
