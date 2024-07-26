package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, String> {
    List<CourseContent> getCourseContentsByCourseIdOrderBySequenceNumber(String courseId);

    @Query(value = "SELECT MAX(con.sequence_number)\n" +
                   "FROM course_contents con\n" +
                   "WHERE con.course_id = :courseId", nativeQuery = true)
    Integer findLastSequenceNumberByCourseId(String courseId);

    @Query(value = "SELECT c.organization_id " +
                   "FROM course_contents cc " +
                   "LEFT JOIN courses c " +
                   "ON cc.course_id = c.id " +
                   "WHERE cc.quiz_id = :quizId " +
                   "GROUP BY c.organization_id", nativeQuery = true)
    Optional<String> findOrganizationIdByQuizIdNative(@Param("quizId") String quizId);

}
