package com.todock.marimo.domain.lesson.entity.hotsitting;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_self_introduce")
public class SelfIntroduce {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long selfIntroduceId;

    @Column(name = "self_int_num") // 자기소개 식별 번호
    private Long selfIntNum;

    @Column(name = "contents")
    private String contents;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "hot_sitting_id")
    private HotSitting hotSitting;

    // 여러 개의 질문/답변을 포함하는 일대다 관계 설정
    @JsonManagedReference
    @OneToMany(mappedBy = "selfIntroduce", cascade = CascadeType.ALL)
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

    public SelfIntroduce(HotSitting hotSitting, Long selfIntNum, String contents) {
        this.hotSitting = hotSitting;
        this.contents = contents;
        this.selfIntNum = selfIntNum;
    }

    public SelfIntroduce(Long selfIntNum, String contents, List<QuestionAnswer> questionAnswers) {
        this.selfIntNum = selfIntNum;
        this.contents = contents;
        this.questionAnswers = questionAnswers;
    }
}
