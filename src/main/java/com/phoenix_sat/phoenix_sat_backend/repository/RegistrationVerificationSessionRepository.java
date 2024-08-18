package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.RegistrationVerificationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationVerificationSessionRepository extends JpaRepository<RegistrationVerificationSession, String> {
    Optional<RegistrationVerificationSession> findByVerificationIdAndIsVerifiedFalse(String verificationId);
}
