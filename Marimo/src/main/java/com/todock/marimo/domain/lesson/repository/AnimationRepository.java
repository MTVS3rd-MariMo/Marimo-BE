package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.avatar.Animation;
import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimationRepository extends JpaRepository<Animation, Long> {

}
