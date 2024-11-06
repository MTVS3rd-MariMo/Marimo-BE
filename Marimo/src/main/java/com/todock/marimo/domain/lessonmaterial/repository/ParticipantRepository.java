package com.todock.marimo.domain.lessonmaterial.repository;

import com.todock.marimo.domain.lesson.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
