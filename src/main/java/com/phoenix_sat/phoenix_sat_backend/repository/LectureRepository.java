package com.phoenix_sat.phoenix_sat_backend.repository;

import com.phoenix_sat.phoenix_sat_backend.entity.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, String> {
    @Transactional
    @Modifying
    @Query(value = """
            update lectures as l
            set is_deleted=:isDeleted
            where l.id IN(select cc.lecture_id from course_contents cc where cc.course_id IN(
            select c.id from courses c where organization_id=:organizationId
            ))
            """, nativeQuery = true)
    void updateIsDeletedByOrganizationId(Boolean isDeleted,String organizationId);
    @Query("select l from Lecture l where l.id IN (select cc.lecture.id from CourseContent cc where cc.course.organization.id=?1)")
    List<Lecture> findAllByOrganizationId(String organizationId);
}
