package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Entity
@Table(name = "course_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
@SQLRestriction("is_deleted = false")
public class CourseDetail extends BaseEntity{
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @OneToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String name;
    private Integer sections;
    private String author;

    @Column(name = "published_date")
    private Date publishedDate;

    @Column(name = "available_points")
    private Integer availablePoints;

    private String categories;
}
