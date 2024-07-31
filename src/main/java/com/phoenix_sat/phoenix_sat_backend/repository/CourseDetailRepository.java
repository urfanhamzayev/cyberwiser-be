package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.CourseDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CourseDetailRepository extends JpaRepository<CourseDetail, String> {
    @Transactional
    @Modifying
    @Query(value = """
            update course_details as cd
            set is_deleted=:isDeleted
            where cd.course_id IN(select c.id from courses c where organization_id=:organizationId)
            """,nativeQuery = true)
    void updateIsDeletedTrueByOrganizationId(Boolean isDeleted, String organizationId);
}
