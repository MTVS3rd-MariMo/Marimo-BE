package com.todock.marimo.domain.user.repository;

import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameDto;
import com.todock.marimo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 유저 만들기
    
    // 유저 아이디로 수업 결과 조회하기
    
    // 유저 아이디로 사진 조회하기


}
