package com.todock.marimo.domain.user.controller;

import com.todock.marimo.domain.user.dto.LoginUserRequestDto;
import com.todock.marimo.domain.user.dto.LoginUserResponseDto;
import com.todock.marimo.domain.user.dto.RegistUserRequestDto;
import com.todock.marimo.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 회원가입
     */

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody RegistUserRequestDto registUserRequestDto) {

        userService.signUp(registUserRequestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 로그인
     */

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> login(@RequestBody LoginUserRequestDto loginUserRequestDto) { // @RequestBody 추가

        LoginUserResponseDto loginDto = userService.login(loginUserRequestDto); // 로그인 후 userId 반환

        if (loginDto != null) {
            return ResponseEntity.ok(loginDto);
        } else {
            return ResponseEntity.status(401).body(loginDto);
        }
    }

    /**
     * 로그아웃
     */
}
