package com.todock.marimo.domain.result.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResult {

    private Long userId;

    private String userName; // 참가자 이름

    private String lessonRole; // 참가자가 맡은 역할

    private String avatarImg; // 참가자가 그린 아바타

    public ParticipantResult(Long userId, String userName, String avatarImg) {
        this.userId = userId;
        this.userName = userName;
        this.avatarImg = avatarImg;
    }
}
