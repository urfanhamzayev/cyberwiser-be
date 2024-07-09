package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.QuizSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizSectionRepository extends JpaRepository<QuizSection, String> {
    Optional<QuizSection> findByQuizId(String quizId);
}
