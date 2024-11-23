package com.todock.marimo.domain.user.service;

import com.todock.marimo.domain.user.dto.LoginUserRequestDto;
import com.todock.marimo.domain.user.dto.LoginUserResponseDto;
import com.todock.marimo.domain.user.dto.SignUpUserRequestDto;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 유저 회원가입
     */
    @Transactional
    public void signUp(SignUpUserRequestDto userInfo) {

        // 입력값 검증
        if (userInfo == null) {
            throw new IllegalArgumentException("회원가입 정보가 제공되지 않았습니다.");
        }
        if (!StringUtils.hasText(userInfo.getName())) {
            throw new IllegalArgumentException("이름(아이디)은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(userInfo.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }
        if (userInfo.getRole() == null) {
            throw new IllegalArgumentException("역할(Role)은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(userInfo.getSchool())) {
            throw new IllegalArgumentException("학교 정보는 필수 입력값입니다.");
        }
        if (userInfo.getGrade() == null || userInfo.getGrade() < 1) {
            throw new IllegalArgumentException("학년은 1 이상의 값이어야 합니다.");
        }
        if (userInfo.getClassRoom() == null || userInfo.getClassRoom() < 1) {
            throw new IllegalArgumentException("반은 1 이상의 값이어야 합니다.");
        }
        // 학생 번호는 학생만 입력해야 함
        if (userInfo.getRole().toString().equalsIgnoreCase("STUDENT") &&
                (userInfo.getStudentNumber() == null || userInfo.getStudentNumber() < 1)) {
            throw new IllegalArgumentException("학생 번호는 1 이상의 값이어야 합니다.");
        }
        // 이미 존재하는 사용자 확인
        if (userRepository.findByNameAndPassword(userInfo.getName(), userInfo.getPassword()) != null) {
            throw new RuntimeException("유저가 이미 존재합니다.");
        }

        // User Entity 객체 생성
        User newUser = new User(
                userInfo.getRole(),
                userInfo.getSchool(),
                userInfo.getGrade(),
                userInfo.getClassRoom(),
                userInfo.getStudentNumber(),
                userInfo.getName(),
                userInfo.getPassword()
        );

        userRepository.save(newUser); // newUser 등록
    }


    /**
     * 유저 로그인
     */
    public LoginUserResponseDto login(LoginUserRequestDto loginInfo) {

        // 입력값 검증
        if (loginInfo == null) {
            throw new IllegalArgumentException("로그인 정보가 제공되지 않았습니다.");
        }
        if (!StringUtils.hasText(loginInfo.getName())) {
            throw new IllegalArgumentException("이름(아이디)은 필수 입력값입니다.");
        }
        if (!StringUtils.hasText(loginInfo.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }

        // 사용자 인증 확인
        User user = userRepository.findByNameAndPassword(
                loginInfo.getName(),
                loginInfo.getPassword()
        );

        if (user == null) {
            throw new RuntimeException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // 로그인 응답 생성
        return new LoginUserResponseDto(
                user.getUserId(),
                user.getRole()
        );
    }

}
