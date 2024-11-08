package com.todock.marimo.domain.lesson.service;

import com.todock.marimo.domain.lesson.dto.AnswerRequestDto;
import com.todock.marimo.domain.lesson.repository.OpenQuestionRepository;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestion;
import com.todock.marimo.domain.lessonmaterial.entity.openquestion.OpenQuestionAnswer;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OpenQuestionService {

    private final OpenQuestionRepository openQuestionRepository;

    @Autowired
    public OpenQuestionService(OpenQuestionRepository openQuestionRepository) {
        this.openQuestionRepository = openQuestionRepository;
    }

    /**
     * 단체 사진 저장
     */
    public void saveAnswer(Long userId, AnswerRequestDto answerDto) {

        OpenQuestion openQuestion = openQuestionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("openQuestionId로 openQuestion을 찾을 수 없습니다."));

        openQuestion.getOpenQuestionAnswerList().add( //  수업의 열린 질문에 답변 저장
                new OpenQuestionAnswer(
                        openQuestion,
                        userId,
                        answerDto.getAnswer()
                )
        );
        openQuestionRepository.save(openQuestion);

    }
}
