package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.hotsitting.SelfIntroduce;
import com.todock.marimo.domain.result.dto.HotSittingResultDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SelfIntroduceRepository extends JpaRepository<SelfIntroduce, Long> {

    // 자기소개 엔티티를 수업Id와 자기소개 식별 번호로 조회
    SelfIntroduce findByHotSitting_hotSittingIdAndSelfIntNum(Long hotSittingId, Long selfIntNum);

    @Query(
            value = "SELECT " +
                    "si.contents AS selfIntroduce, " +
                    "GROUP_CONCAT(qa.qnA_contents SEPARATOR ',') AS questionAnswers " +
                    "FROM tbl_self_introduce si " +
                    "JOIN tbl_question_answer qa ON si.self_introduce_id = qa.self_introduce_id " +
                    "WHERE si.hot_sitting_id = :hotSittingId " +
                    "GROUP BY si.self_introduce_id",
            nativeQuery = true
    )
    List<Object[]> findSelfIntroduceFetch(@Param("hotSittingId") Long hotSittingId);
}
