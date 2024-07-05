package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "part")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Part {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    private String title;
    private String duration;

    @ManyToOne
    @JoinColumn(name = "content_id")
    private CourseContent courseContent;

    @JoinColumn(name = "video_url")
    private String videoUrl;

    @Column(name = "sequence_number")
    private Integer sequenceNumber;
}