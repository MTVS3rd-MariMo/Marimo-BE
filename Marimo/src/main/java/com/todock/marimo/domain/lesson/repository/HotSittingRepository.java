package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.hotsitting.HotSitting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotSittingRepository extends JpaRepository<HotSitting, Long> {

    // selfIntNum, lessonId로 유저 검색
    HotSitting findByLesson_lessonIdAndSelfIntNum(Long lessonId, Long selfIntNum);
}
