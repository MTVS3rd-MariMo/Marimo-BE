package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.hotsitting.SelfIntroduce;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SelfIntroduceRepository extends JpaRepository<SelfIntroduce, Long> {

    SelfIntroduce findByHotSitting_hotSittingIdAndSelfIntNum(Long hotSittingId, Long selfIntNum);

    @Query(
            value = "SELECT " +
                    "si.contents AS selfIntroduce, " +
                    "si.user_id AS userId, " +
                    "GROUP_CONCAT(qa.qnA_contents SEPARATOR '-') AS questionAnswers " +
                    "FROM tbl_self_introduce si " +
                    "JOIN tbl_question_answer qa ON si.self_introduce_id = qa.self_introduce_id " +
                    "WHERE si.hot_sitting_id = :hotSittingId " +
                    "GROUP BY si.self_introduce_id",
            nativeQuery = true
    )
    List<Object[]> findSelfIntroduceFetch(@Param("hotSittingId") Long hotSittingId);
}
