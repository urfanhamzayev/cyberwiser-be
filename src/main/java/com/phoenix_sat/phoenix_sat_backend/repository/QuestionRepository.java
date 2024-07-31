package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    @Query(value = "select * from questions as q where q.quiz_section_id=:quizSectionId", nativeQuery = true)
    List<Question> findAllByQuizSectionId(String quizSectionId);

    @Transactional
    @Modifying
    @Query(value = """
            update questions as q
            set is_deleted=:isDeleted
            where q.quiz_section_id\s
            IN(select qs.id from quiz_sections qs where qs.quiz_id
            IN(select cc.quiz_id from course_contents cc where cc.course_id
            IN (select c.id from courses c where c.organization_id=:organizationId)))
            """,nativeQuery = true)
    void updateIsDeletedByOrganizationId(Boolean isDeleted, String organizationId);

    @Query("select q from Question q where q.quizSection.quiz.id IN (select cc.quiz.id from CourseContent cc where cc.course.organization.id=:organizationId)")
    List<Question> findAllByOrganizationId(String organizationId);

}
