package com.todock.marimo.domain.lesson.entity.avatar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
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

    @Column(name="animation")
    private String animation;
}