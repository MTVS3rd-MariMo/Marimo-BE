package com.todock.marimo.domain.user.controller;

import com.todock.marimo.domain.user.dto.LoginUserRequestDto;
import com.todock.marimo.domain.user.dto.LoginUserResponseDto;
import com.todock.marimo.domain.user.dto.RegistUserRequestDto;
import com.todock.marimo.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    /**
     * 회원가입
     */
    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody RegistUserRequestDto registUserRequestDto) {

        log.info("회원가입 하는 유저의 정보 : {}", registUserRequestDto);

        userService.signUp(registUserRequestDto);

        return ResponseEntity.ok().build();
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> login(@RequestBody LoginUserRequestDto loginUserRequestDto) { // @RequestBody 추가

        log.info("로그인 하려는 유저의 정보로 로그인 Dto: {}", loginUserRequestDto);

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
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody LoginUserRequestDto loginUserRequestDto) {

        return ResponseEntity.ok().body("loginUserRequestDto");
    }
}
