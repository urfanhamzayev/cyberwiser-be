package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.converter.SelectionTypeConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import com.phoenix_sat.phoenix_sat_backend.enums.SelectionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "quiz_section")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class Question {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private QuizSection quizSection;

    private String text;

    @Enumerated(EnumType.STRING)
    @Type(value = SelectionTypeConverter.class)
    @Column(name = "selection_type")
    private SelectionType selectionType;
    
    @Column(name = "question_options")
    private String questionOptions;

    @Column(name = "correct_option")
    private String correctOption;
}
