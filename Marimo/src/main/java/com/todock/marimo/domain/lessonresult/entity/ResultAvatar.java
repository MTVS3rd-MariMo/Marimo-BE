package com.todock.marimo.domain.lessonresult.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ResultAvatar {

    private Long userId; // 유저 id

    private String role; // 역할

    private String avatarImg; // 아바타 img
}
