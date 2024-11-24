package com.todock.marimo.domain.user.service;

import com.todock.marimo.domain.user.dto.SignUpUserRequestDto;
import com.todock.marimo.domain.user.entity.Role;
import com.todock.marimo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private UserService userService;

    public UserServiceTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Stream<Arguments> newUser() {
        return Stream.of(
                Arguments.of(new SignUpUserRequestDto(
                        "정현민",
                        "mark9483",
                        Role.TEACHER,
                        "메타버스 고등학교",
                        1,
                        1,
                        31
                ))
        );
    }


    @DisplayName("유저 회원가입")
    @Test
    void signUp(SignUpUserRequestDto userInfo) {
        // given
        Assertions.assertDoesNotThrow(
                () -> userService.signUp(userInfo)
        );
        // then
    }

    @Test
    void login() {
    }

}