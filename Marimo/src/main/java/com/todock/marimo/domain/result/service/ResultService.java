package com.todock.marimo.domain.result.service;

import com.todock.marimo.domain.lesson.entity.Lesson;
import com.todock.marimo.domain.lesson.repository.LessonRepository;
import com.todock.marimo.domain.lesson.repository.ParticipantRepository;
import com.todock.marimo.domain.lessonmaterial.entity.LessonMaterial;
import com.todock.marimo.domain.lessonmaterial.repository.LessonMaterialRepository;
import com.todock.marimo.domain.result.dto.StudentResultDto;
import com.todock.marimo.domain.result.dto.TeacherResultDto;
import com.todock.marimo.domain.result.dto.LessonResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService {

    private final LessonRepository lessonRepository;
    private final ParticipantRepository participantRepository;
    private final LessonMaterialRepository lessonMaterialRepository;

    @Autowired
    public ResultService(
            LessonRepository lessonRepository
            , ParticipantRepository participantRepository, LessonMaterialRepository lessonMaterialRepository) {
        this.lessonRepository = lessonRepository;
        this.participantRepository = participantRepository;
        this.lessonMaterialRepository = lessonMaterialRepository;
    }


    /**
     * 학생이 참가한 모든 수업 리스트 조회 (사진 리스트로 보여줌) - LessonId, photoList 반환
     */
    public List<StudentResultDto> findAllPhotos(Long userId) {

        return participantRepository.findAllByUserId(userId)
                .stream()
                .map(participant -> new StudentResultDto(
                        participant.getLesson().getLessonId(),
                        participant.getLesson().getPhotoUrl()))
                .collect(Collectors.toList());
    }


    /**
     * 학생이 참가한 수업 사진 상세 조회 - photo 반환
     */
    /*
    public String findPhotoByLessonId(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("lessonId로 수업을 찾을수 없습니다."));

        return lesson.getPhotoUrl();
    }*/


    /**
     * 선생님이 참가한 모든 수업 조회
     */
//    public List<TeacherResultDto> findAllLessons(Long userId) {
//
//        participantRepository.findAllByUserId(userId);
//         TeacherResultDto teacherResultDto;
//       return teacherResultDto;
//
//    }


    /**
     * 선생님이 참가한 수업 상세 조회
     */
    public LessonResultDto lessonDetail(Long lessonId) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("lessonId로 수업을 찾을 수 없습니다."));

        LessonMaterial lessonMaterial = lessonMaterialRepository.findById(lesson.getLessonMaterialId())
                .orElseThrow(() -> new IllegalArgumentException("lessonMaterialId로 수업 자료를 찾을 수 없습니다."));

        LessonResultDto lessonResultDto = new LessonResultDto();

//        LessonResultDto lessonResultDto = new LessonResultDto(
//                lessonMaterial.getBookTitle(),
//                lessonMaterial.getBookContents(),
//                lessonMaterial.getQuizList(),
//                lessonMaterial.getOpenQuestionList(),
//                lessonMaterial.getLessonRoleList(),
//                lesson.getParticipantList(),
//                lesson.getAvatarList(),
//                lesson.getHotSitting(),
//                lesson.getPhotoUrl()
//                )

        return lessonResultDto;

    }
}
