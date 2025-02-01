package com.todock.marimo.domain.lesson.repository;

import com.todock.marimo.domain.lesson.entity.Participant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByUserId(Long userId);
}