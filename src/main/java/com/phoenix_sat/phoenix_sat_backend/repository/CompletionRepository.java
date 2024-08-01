package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Completion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompletionRepository extends JpaRepository<Completion, String> {
    Optional<Completion> findByCourseIdAndUserIdAndIsDeletedFalse(String courseId, String userId);

    @Transactional
    @Modifying
    @Query(value = """
            update completions as c
            set is_deleted=:isDeleted
            where c.user_id IN(select u.id from users u where organization_id=:organizationId)
            """,nativeQuery = true)
    void updateIsDeletedTrueByOrganizationId(Boolean isDeleted, String organizationId);


    @Query("select c from Completion c where c.user.organization.id=:organizationId")
    List<Completion> findByOrganizationId(String organizationId);
}
