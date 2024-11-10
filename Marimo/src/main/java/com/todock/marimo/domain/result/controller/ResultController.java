package com.todock.marimo.domain.result.controller;

import com.todock.marimo.domain.result.dto.LessonResultDto;
import com.todock.marimo.domain.result.dto.StudentResultDto;
import com.todock.marimo.domain.result.dto.TeacherResultDto;
import com.todock.marimo.domain.result.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/result")
public class ResultController {


    private final ResultService resultService;

    @Autowired
    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회 (사진 리스트로 보여줌) - LessonId, photoList 반환
     */
    @GetMapping("/student")
    public ResponseEntity<List<StudentResultDto>> getPhotoList(
            @RequestHeader("userId") Long userId) {

        return ResponseEntity.ok().body(resultService.findAllPhotos(userId));
    }


    /**
     * 학생이 참가한 수업 사진 조회 - photo 반환
     *//*
    @GetMapping("/{lessonId}")
    public ResponseEntity<String> getPhoto(@PathVariable("lessonId") Long lessonId) {

        return ResponseEntity.ok(resultService.findPhotoByLessonId(lessonId));

    }*/


    /**
     * 선생님이 참가한 모든 수업 조회 ( 참가자 이름?)
     */
//    @GetMapping
//    public ResponseEntity<List<TeacherResultDto>> getLessonList(
//            @RequestHeader("userId") Long userId) {
//
//        return ResponseEntity.ok().body(resultService.findAllLessons(userId));
//    }


    /**
     * 선생님이 참가한 수업 상세 조회
     */
    @GetMapping("/teacher/{lessonId}")
    public ResponseEntity<LessonResultDto> getLesson(@PathVariable("lessonId") Long lessonId) {

        return ResponseEntity.ok().body(resultService.lessonDetail(lessonId));
    }
}
