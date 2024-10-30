package com.todock.marimo.domain.user.service;

import com.todock.marimo.domain.user.dto.RegistUserRequestDto;
import com.todock.marimo.domain.user.entity.User;
import com.todock.marimo.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // final 키워드가 있는 필드만 의존성 주입을 해준다.
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 회원가입
     */

    @Transactional
    public void signUp(RegistUserRequestDto userInfo) {
        
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

}
