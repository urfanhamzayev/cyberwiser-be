package com.phoenix_sat.phoenix_sat_backend.spesification;

import com.phoenix_sat.phoenix_sat_backend.entity.Course;
import com.phoenix_sat.phoenix_sat_backend.model.request.CourseFilterRequest;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
public class CourseSpecification implements Specification<Course> {
    private Boolean isSuperAdmin;
    private CourseFilterRequest filter;
    private String defaultOrganizationId;


    @Override
    public Predicate toPredicate(Root<Course> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        if (!isSuperAdmin)
            predicates.add(cb.equal(root.get("isVisible"), true));

        if (filter.getOrganizationId() != null) {
            Predicate orgIdPredicate = cb.equal(root.get("organization").get("id"), filter.getOrganizationId());
            Predicate defaultOrgIdPredicate = cb.equal(root.get("organization").get("id"), defaultOrganizationId);
            predicates.add(cb.or(orgIdPredicate, defaultOrgIdPredicate));
        } else {
            // If no organizationId is provided in the filter, still include default organization ID courses
            predicates.add(cb.equal(root.get("organization").get("id"), defaultOrganizationId));
        }

        if (StringUtils.isNotEmpty(filter.getName())) {
            predicates.add(cb.like(cb.lower(root.get("name")), prepareSearchText(filter.getName())));
        }

        if (StringUtils.isNotEmpty(filter.getTags())) {
            predicates.add(cb.like(cb.lower(root.get("tags")), "%" + filter.getTags().toLowerCase() + "%"));
        }


        if (StringUtils.isNotEmpty(filter.getInstructor())) {
            predicates.add(cb.like(cb.lower(root.get("instructor")), "%" + filter.getInstructor().toLowerCase() + "%"));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private String prepareSearchText(String searchText) {
        return "%" + searchText.toLowerCase() + "%";
    }
}
