package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
    @Query(value = "select * from questions as q where q.quiz_section_id=:quizSectionId", nativeQuery = true)
    List<Question> findAllByQuizSectionId(String quizSectionId);

}
