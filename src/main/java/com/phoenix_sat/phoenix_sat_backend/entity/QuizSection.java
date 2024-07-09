package com.phoenix_sat.phoenix_sat_backend.entity;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "quiz_sections")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class QuizSection extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @OneToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    private String title;
}
