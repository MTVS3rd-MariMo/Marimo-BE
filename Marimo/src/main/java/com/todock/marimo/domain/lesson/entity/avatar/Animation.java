package com.todock.marimo.domain.lesson.entity.avatar;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tbl_animation")
public class Animation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long animationId; // 애니메이션 id

    // 수업은 여러개의 아바타를 가진다.
    @ManyToOne
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;

    @Column(name = "animation1")
    private String animation1;

    @Column(name = "animation2")
    private String animation2;
}