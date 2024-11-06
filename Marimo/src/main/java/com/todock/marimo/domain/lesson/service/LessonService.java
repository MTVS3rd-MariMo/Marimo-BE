package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.entity.Participant;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lessonmaterial.dto.LessonMaterialNameResponseDto;
import com.todock.marimo.domain.lessonmaterial.repository.ParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class LessonService {

    private final LessonRepository lessonRepository;
    private final ParticipantRepository participantRepository;

    @Autowired
    public LessonService(LessonRepository lessonRepository, ParticipantRepository participantRepository) {
        this.lessonRepository = lessonRepository;
        this.participantRepository = participantRepository;
    }



    /**
     * LessonId로 참가자 목록에 유저Id 추가하기
     */
    public void updateUserByLessonId(Long userId, Long lessonId) {
        // 수업 찾기
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("수업을 찾을 수 없습니다."));

        // 참가자 생성
        Participant participant = new Participant();
        participant.setUserId(userId);
        participant.setLesson(lesson);

        // 참가자 저장
        participantRepository.save(participant);
    }


}