package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.QuizSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSectionRepository extends JpaRepository<QuizSection, String> {
    Optional<QuizSection> findByQuizId(String quizId);

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE quiz_sections qs
            SET is_deleted = :isDeleted
            WHERE qs.quiz_id
            IN (
            SELECT cc.quiz_id
            FROM course_contents cc
            WHERE cc.course_id
            IN (
            SELECT c.id from courses c where c.organization_id=:organizationId
            )
            )
            """,nativeQuery = true)
    void updateIsDeletedByOrganizationId(Boolean isDeleted, String organizationId);
    @Query("select qs from QuizSection qs where qs.quiz.id IN(select cc.quiz.id from CourseContent cc where cc.course.organization.id=:organizationId)")
    List<QuizSection> findAllByOrganizationId(String organizationId);
}
