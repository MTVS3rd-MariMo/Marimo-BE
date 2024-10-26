package com.todock.marimo.domain.lessonresult.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ResultParticipant {

    @Column(name="user_id")
    private Long userId; // 유저 id

    @Column(name = "name")
    private String name; // 유저 이름
    
    @Column(name = "role_name")
    private String roleName;  // 맡은 역할

}