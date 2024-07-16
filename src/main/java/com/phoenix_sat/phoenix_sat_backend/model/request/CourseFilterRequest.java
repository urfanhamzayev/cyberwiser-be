package com.phoenix_sat.phoenix_sat_backend.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseFilterRequest {
    private String name;
    private String tags;
    private String instructor;
    @JsonIgnore
    private String organizationId;
}
