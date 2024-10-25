//package com.todock.marimo.erd.domain.lessonmaterial.entity.quiz;
//
//import com.todock.marimo.erd.domain.lessonmaterial.entity.LessonMaterial;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//@Table(name="tbl_selected_quiz")
//public class SelectedQuiz {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long selected_quiz_id;
//
//    // 수업 준비는 여러개의 선택된 퀴즈을 가질 수 있다.
//    @ManyToOne
//    @JoinColumn(name = "lesson_material_id", nullable = false)
//    private LessonMaterial lessonMaterial;
//
//    // 선택된 퀴즈는 여러개의 퀴즈를 가질 수 있다.
//    @OneToMany(mappedBy = "selectedQuiz")
//    private List<Quiz> quizList = new ArrayList<>();
//
//}
