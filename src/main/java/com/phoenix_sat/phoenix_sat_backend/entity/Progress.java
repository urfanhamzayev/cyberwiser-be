package com.phoenix_sat.phoenix_sat_backend.entity;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

@Entity
@Table(name = "progress")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class Progress extends BaseEntity{
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private CourseContent content;

    @Column(name = "lectures_completed")
    private int lecturesCompleted;

    @Column(name = "quizzesCompleted")
    private int quizzesCompleted;
}

