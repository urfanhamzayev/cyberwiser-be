package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {

    List<Progress> findAllByUserIdAndCourseIdOrderByCreateDate(String userId, String courseId);

    @Query("select p from Progress p where p.content.lecture.id = :lectureId AND p.user.id=:userId")
    Optional<Progress> findByLectureIdAndUserId(String lectureId, String userId);

    @Query("select p from Progress p where p.content.quiz.id = :quizId AND p.user.id=:userId")
    Optional<Progress> findByQuizIdAndUserId(String quizId, String userId);


    @Query("select p.isCompleted from Progress  p where p.course.id=:courseId and p.user.id=:userId")
    List<Boolean> findIsCompletedByCourseIdAndUserId(String courseId, String userId);
}
