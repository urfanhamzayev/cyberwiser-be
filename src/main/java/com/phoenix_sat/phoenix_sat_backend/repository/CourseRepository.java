package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {
    @Transactional
    @Modifying
    @Query("update Course c set c.isVisible = true where c.id = :id")
    void updateById(String id);

    List<Course> findAllByOrganizationId(String organizationId);

    Optional<Course> findByIdAndIsDeletedFalseAndIsVisibleTrue(String courseId);

    @Transactional
    @Modifying
    @Query("Update Course c set c.isVisible =:isActive , c.isDeleted =:isDeleted where c.organization.id=:organizationId")
    void updateCoursesIsActiveAndIsDeletedByOrganizationId(Boolean isActive, Boolean isDeleted, String organizationId);
}
