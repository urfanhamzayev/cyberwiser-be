package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import com.phoenix_sat.phoenix_sat_backend.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, String> {
    Optional<CourseAssignment> findByCourseId(String courseId);

    @Query("select ca.course from CourseAssignment  ca where ca.organization.id=:subOrgId or ca.organization.id=:mainOrgId and ca.confirmed = true")
    List<Course> findAllByOrganizationIdAndMainOrganizationId(String subOrgId, String mainOrgId);
}
