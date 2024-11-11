package com.todock.marimo.domain.result.dto;

import com.todock.marimo.domain.lesson.entity.avatar.Avatar;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResultDto {

    private String userName; // 참가자 id

    private String avatarUrl;

    private String character;

    public ParticipantResultDto(String userName, String avatarUrl) {
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }
}

