package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseAssignmentRepository extends JpaRepository<CourseAssignment, String>{
    Optional<CourseAssignment> findByCourseId(String courseId);
}
