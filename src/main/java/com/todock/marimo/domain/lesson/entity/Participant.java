package com.todock.marimo.domain.lesson.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId; //  참가자 id

    @Column(name = "userId")
    private Long userId;

    @Column(name = "participant_name") // 참가자 이름
    private String participantName;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @Override
    public String toString() {
        return "Participant{" +
                "participantName='" + participantName + '\'' +
                '}';
    }
}
