package com.todock.marimo.domain.lesson.controller;

import com.todock.marimo.domain.lesson.dto.WavFileToAIRequestDto;
import com.todock.marimo.domain.lesson.dto.WavFileToServerRequestDto;
import com.todock.marimo.domain.lesson.service.HotSittingService;
import com.todock.marimo.domain.lessonresult.dto.SelfIntroduceRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/hot-sitting")
public class HotSittingController {

    private final HotSittingService hotSittingService;

    @Autowired
    public HotSittingController(HotSittingService hotSittingService) {
        this.hotSittingService = hotSittingService;
    }

    /**
     * 핫시팅 자기소개 저장
     */
    @PutMapping("/self-introduce")
    public ResponseEntity<String> hotSittingSelfIntroduce(
            @RequestHeader("userId") Long userId
            , @RequestBody SelfIntroduceRequestDto selfIntroduceRequestDto) {

        hotSittingService.registIntroduce(userId, selfIntroduceRequestDto);

        return ResponseEntity.ok().body("자기소개를 저장했습니다.");
    }

    /**
     * 핫시팅 wavFile AI서버로 전달 - introduceId 추가해서 전달
     */
    @PostMapping("/wav-file")
    public ResponseEntity<String> hotSittingWavFile(
            @ModelAttribute WavFileToAIRequestDto wavDto) {
        log.info("lessonId : {}, userName : {}, character : {}, SelfIntNum : {}"
                , wavDto.getLessonId(), wavDto.getUserName(), wavDto.getCharacter(), wavDto.getSelfIntNum());

        hotSittingService.sendWavToAiServer(wavDto);

        return ResponseEntity.ok().body("정상적으로 전송되었습니다.");
    }

}
