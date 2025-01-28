package com.todock.marimo.domain.user.repository;

import com.todock.marimo.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이름과 비밀번호로 유저 찾기
    User findByNameAndPassword(String name, String password);

}
