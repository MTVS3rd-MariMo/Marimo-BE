package com.todock.marimo.domain.lesson.entity.avatar;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_animation")
public class Animation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long animationId; // 애니메이션 id

    // 수업은 여러개의 아바타를 가진다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_id")
    @JsonBackReference
    private Avatar avatar;

    @Column(name = "animation_idle")
    private String animationIdle;

    @Column(name = "animation_walk")
    private String animationWalk;

    @Override
    public String toString() {
        return "Animation{" +
                "animationIdle='" + animationIdle + '\'' +
                ", animationWalk='" + animationWalk + '\'' +
                '}';
    }
}