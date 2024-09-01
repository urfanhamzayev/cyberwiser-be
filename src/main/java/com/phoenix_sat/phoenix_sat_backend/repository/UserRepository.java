package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmailAndIsActiveTrue(String email);

    Optional<User> findByIdAndIsActiveTrue(String id);

    Optional<User> findByIdAndIsActiveFalse(String id);

    Boolean existsByEmailAndIsActiveTrueAndIsDeletedFalse(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.isActive = :isActive, u.isDeleted = :isDeleted where u.id =:userId")
    void updateIsActiveAndIsDeletedById(String userId,Boolean isActive,Boolean isDeleted);

    @Query("Select u from User u where u.organization.id=:organizationId and u.isActive=true")
    List<User> findUsersByOrganizationIdAndIsActiveTrue(String organizationId);

    @Transactional
    @Modifying
    @Query("update User u set u.isActive = :isActive, u.isDeleted = :isDeleted where u.organization.id =:organizationId")
    void updateUsersIsActiveByOrganizationId(Boolean isActive, Boolean isDeleted, String organizationId);

    List<User> findAllByOrganizationId(String orgId);
}
