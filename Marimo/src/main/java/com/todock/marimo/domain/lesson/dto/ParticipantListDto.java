package com.todock.marimo.domain.lesson.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListDto {

    private List<Long> userIds;
}
