package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@SQLRestriction("is_deleted = false")
public class Quiz extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    private String title;

    @Column(name = "number_of_questions")
    private int numberOfQuestions;
}
