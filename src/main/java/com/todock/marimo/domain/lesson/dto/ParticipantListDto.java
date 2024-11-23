package com.todock.marimo.domain.lesson.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListDto {

    @NotEmpty(message = "참가자가 없습니다.")
    private List<Long> userIds;
}
