package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, String> {

    @Transactional
    @Modifying
    @Query(value = """
            UPDATE quizzes q
            SET is_deleted = :isDeleted
            WHERE q.id
            IN (
            SELECT cc.quiz_id FROM course_contents cc WHERE cc.course_id
            IN (
            SELECT c.id from courses c where c.organization_id=:organizationId
            )
            )
            """,nativeQuery = true)
    void updateIsDeletedByOrganizationId(Boolean isDeleted, String organizationId);

    @Query("select q from Quiz q where q.id IN (select cc.quiz.id from CourseContent cc where cc.course.organization.id=:organizationId)")
    List<Quiz> findAllByOrganizationId(String organizationId);
}
