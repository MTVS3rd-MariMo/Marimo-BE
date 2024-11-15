package com.todock.marimo.domain.user.service;

import com.todock.marimo.domain.user.dto.LoginUserRequestDto;
import com.todock.marimo.domain.user.dto.LoginUserResponseDto;
import com.todock.marimo.domain.user.dto.RegistUserRequestDto;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void signUp(RegistUserRequestDto userInfo) {

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

        // 사용자 인증 확인
        User user = userRepository.findByNameAndPassword(
                loginInfo.getName(),
                loginInfo.getPassword()
        );

        LoginUserResponseDto loginDto = new LoginUserResponseDto(
                user.getUserId(),
                user.getRole()
        );

        // 유저가 존재하면 userId 반환
        return loginDto;
    }


}
