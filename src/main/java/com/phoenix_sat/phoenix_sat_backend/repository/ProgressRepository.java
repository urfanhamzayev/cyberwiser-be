package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, String> {
    Optional<Progress> findByUserId(String userId);

}
