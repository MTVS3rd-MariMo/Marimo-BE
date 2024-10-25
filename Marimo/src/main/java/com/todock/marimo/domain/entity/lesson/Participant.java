package com.todock.marimo.domain.entity.lesson;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_participant")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId; //  참가자 id

    @Column(name = "participant_name") // 참가자 이름
    private String participantName;

    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

//    @ManyToOne
//    @JoinColumn(name = "lesson_result_id")
//    private LessonResult lessonResult;


}
