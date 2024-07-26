package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Completion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompletionRepository extends JpaRepository<Completion, String> {
    Optional<Completion> findByCourseIdAndUserId(String courseId, String userId);
}
