package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseContent;
import com.phoenix_sat.phoenix_sat_backend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {
    @Query("SELECT p FROM Progress p WHERE p.user.id = :userId AND p.course.id = :courseId AND p.isDeleted = false ORDER BY p.isCompleted DESC, p.createDate ASC")
    List<Progress> findAllByUserIdAndCourseIdOrderByIsCompletedDescCreateDateAsc(String userId, String courseId);

    @Query("select p from Progress p where p.content.lecture.id = :lectureId AND p.user.id=:userId")
    Optional<Progress> findByLectureIdAndUserIdAndIsDeletedFalse(String lectureId, String userId);

    @Query("select p from Progress p where p.content.quiz.id = :quizId AND p.user.id=:userId")
    Optional<Progress> findByQuizIdAndUserIdAndIsDeletedFalse(String quizId, String userId);


    @Query("select p.isCompleted from Progress  p where p.course.id=:courseId and p.user.id=:userId")
    List<Boolean> findIsCompletedByCourseIdAndUserId(String courseId, String userId);

    @Transactional
    @Modifying
    @Query(value = """
            update progress as p
            set is_deleted=:isDeleted
            where p.user_id IN(select u.id from users u where u.organization_id=:organizationId)
            """, nativeQuery = true)
    void updateIsDeletedByOrganizationId(Boolean isDeleted, String organizationId);

    @Query("select p from Progress p where p.user.organization.id =:organizationId")
    List<Progress> findAllByOrganizationId(String organizationId);

    @Query("SELECT p.content FROM Progress p " +
           "WHERE p.user.id = :userId " +
           "AND p.isCompleted = true " +
           "ORDER BY p.createDate DESC")
    List<CourseContent> findLastCompletedContentsByUserId(String userId);

    default Optional<CourseContent> getLastCompletedContentByUserId(String userId) {
        List<CourseContent> courseContents = findLastCompletedContentsByUserId(userId);

        return courseContents.isEmpty()? Optional.empty() : Optional.of(courseContents.get(0));
    }
}
