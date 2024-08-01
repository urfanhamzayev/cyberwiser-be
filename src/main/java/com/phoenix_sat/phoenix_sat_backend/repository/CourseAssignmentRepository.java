package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import com.phoenix_sat.phoenix_sat_backend.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, String>, JpaSpecificationExecutor<CourseAssignment> {
    Optional<CourseAssignment> findByIdAndIsDeletedFalse(String id);

    @Query("""
            select ca
            from CourseAssignment ca
            where ca.organization.id=:subOrgId
            and ca.isDeleted =false
            """)
    List<CourseAssignment> findAllByOrganizationIdAndIsDeletedFalse(String subOrgId);



    @Transactional
    @Modifying
    @Query("update CourseAssignment ca set ca.isDeleted=:isDeleted where ca.organization.id =:organizationId")
    void updateIsDeletedByOrganizationId(Boolean isDeleted, String organizationId);

    Optional<CourseAssignment> findByCourseIdAndOrganizationIdAndConfirmedTrue(String courseId, String organizationId);
}
