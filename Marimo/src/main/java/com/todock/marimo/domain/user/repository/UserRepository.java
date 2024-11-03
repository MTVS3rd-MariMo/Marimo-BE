package com.todock.marimo.domain.user.repository;

import com.todock.marimo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // 이름과 비밀번호로 유저 찾기
    User findByNameAndPassword(String name, String password);

    // 유저 만들기
    
    // 유저 아이디로 수업 결과 조회하기
    
    // 유저 아이디로 사진 조회하기


}
