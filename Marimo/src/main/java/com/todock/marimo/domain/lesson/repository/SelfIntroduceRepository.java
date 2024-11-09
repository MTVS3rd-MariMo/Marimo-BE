package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.hotsitting.SelfIntroduce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelfIntroduceRepository extends JpaRepository<SelfIntroduce, Long> {

    // 자기소개 엔티티를 수업Id와 자기소개 식별 번호로 조회
    SelfIntroduce findByHotSitting_HotSittingIdAndSelfIntNum(Long lessonId, Long selfIntNum);

}
