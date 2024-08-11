package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, String> {
    List<CourseContent> getCourseContentsByCourseIdAndIsDeletedFalseOrderBySequenceNumber(String courseId);

    @Query(value = "SELECT MAX(con.sequence_number)\n" +
                   "FROM course_contents con\n" +
                   "WHERE con.course_id = :courseId", nativeQuery = true)
    Integer findLastSequenceNumberByCourseIdIsDeletedFalse(String courseId);

    @Query(value = "SELECT c.organization_id " +
                   "FROM course_contents cc " +
                   "LEFT JOIN courses c " +
                   "ON cc.course_id = c.id " +
                   "WHERE cc.quiz_id = :quizId and cc.is_deleted=false" +
                   "GROUP BY c.organization_id", nativeQuery = true)
    Optional<String> findOrganizationIdByQuizIdNativeIsDeletedFalse(@Param("quizId") String quizId);

    @Transactional
    @Modifying
    @Query(value = """
            update course_contents as cc
            set is_deleted=:isDeleted
            where cc.course_id IN(select c.id from courses c where organization_id=:organizationId)
            """,nativeQuery = true)
    void updateIsDeletedByCourseId(Boolean isDeleted, String organizationId);

    @Query("select cc from CourseContent cc where cc.course.organization.id=:organizationId")
    List<CourseContent> findAllByOrganizationId(String organizationId);

    Optional<CourseContent> findByQuizId(String quizId);

    @Query("select coalesce(cc.sequenceNumber,0) from CourseContent cc where cc.lecture.id=:lectureId")
    Integer findSequenceNumberByLectureId(String lectureId);

    Optional<CourseContent> findByLectureIdAndIsDeletedFalse(String lectureId);
}
