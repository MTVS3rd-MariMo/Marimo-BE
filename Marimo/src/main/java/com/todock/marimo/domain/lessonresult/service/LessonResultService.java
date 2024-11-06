package com.todock.marimo.domain.lessonresult.service;

import com.todock.marimo.domain.lesson.dto.LessonOpenQuestionRequestDto;
import com.todock.marimo.domain.lessonmaterial.dto.OpenQuestionAnswerDto;
import com.todock.marimo.domain.lessonmaterial.dto.OpenQuestionDto;
import com.todock.marimo.domain.lessonresult.entity.LessonResult;
import com.todock.marimo.domain.lessonresult.entity.resultopenquestion.ResultOpenQuestion;
import com.todock.marimo.domain.lessonresult.entity.resultopenquestion.ResultOpenQuestionAnswer;
import com.todock.marimo.domain.lessonresult.repository.LessonResultRepository;
import com.todock.marimo.domain.lessonresult.repository.ResultOpenQuestionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonResultService {

    private final LessonResultRepository lessonResultRepository;
    private final ResultOpenQuestionRepository resultOpenQuestionRepository;

    @Autowired
    public LessonResultService(
            LessonResultRepository lessonResultRepository
            , ResultOpenQuestionRepository resultOpenQuestionRepository) {
        this.lessonResultRepository = lessonResultRepository;
        this.resultOpenQuestionRepository = resultOpenQuestionRepository;
    }

    /**
     * 열린 질문 결과 저장
     */
    @Transactional
    public void updateLessonResult(LessonOpenQuestionRequestDto lessonOpenQuestionRequestDto) {

        // LessonResult 엔티티를 생성하거나 찾는다.
        LessonResult lessonResult = lessonResultRepository.findById(lessonOpenQuestionRequestDto.getLessonId())
                .orElseThrow(() -> new EntityNotFoundException("Lesson not found"));

        // 여러 개의 열린 질문을 처리하기 위해 openQuestionList를 반복
        for (OpenQuestionDto openQuestionDto : lessonOpenQuestionRequestDto.getOpenQuestionList()) {
            // 각 열린 질문에 대해 ResultOpenQuestion 객체 생성
            ResultOpenQuestion resultOpenQuestion = new ResultOpenQuestion();
            resultOpenQuestion.setQuestion(openQuestionDto.getQuestion()); // 열린 질문 제목 설정
            resultOpenQuestion.setLessonResult(lessonResult);

            // 열린 질문에 대한 답변 리스트 생성 및 초기화
            List<ResultOpenQuestionAnswer> openQuestionAnswers = new ArrayList<>();

            // DTO에서 전달받은 각 답변을 ResultOpenQuestionAnswer 객체로 변환하여 리스트에 추가
            for (OpenQuestionAnswerDto answerDto : openQuestionDto.getOpenQuestionAnswerList()) {
                ResultOpenQuestionAnswer answer = new ResultOpenQuestionAnswer();
                answer.setResultOpenQuestion(resultOpenQuestion); // 열린 질문과의 연관 설정
                answer.setUserId(answerDto.getUserId()); // 사용자 ID 설정
                answer.setAnswer(answerDto.getAnswer()); // 답변 내용 설정
                openQuestionAnswers.add(answer); // 리스트에 답변 추가
            }

            // 열린 질문에 답변 리스트 설정
            resultOpenQuestion.setOpenQuestionAnswerList(openQuestionAnswers);

            // LessonResult에 열린 질문 추가
            lessonResult.getResultOpenQuestionList().add(resultOpenQuestion);
        }

        // lessonResultRepository를 통해 LessonResult와 연관된 모든 데이터 저장
        lessonResultRepository.save(lessonResult);

    }


}