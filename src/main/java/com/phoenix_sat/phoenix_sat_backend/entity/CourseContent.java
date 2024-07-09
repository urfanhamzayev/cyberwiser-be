    package com.phoenix_sat.phoenix_sat_backend.entity;

    import com.phoenix_sat.phoenix_sat_backend.entity.converter.ContentTypeConverter;
    import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
    import com.phoenix_sat.phoenix_sat_backend.enums.ContentType;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.GenericGenerator;
    import org.hibernate.annotations.Type;

    @Entity
    @Table(name = "course_contents")
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    @EqualsAndHashCode(callSuper = true)
    public class CourseContent extends BaseEntity {
        @Id
        @GeneratedValue(generator = "idGenerator")
        @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
        private String id;

        @ManyToOne
        @JoinColumn(name = "course_id")
        private Course course;

        @OneToOne
        @JoinColumn(name = "lecture_id")
        private Lecture lecture;

        @OneToOne
        @JoinColumn(name = "quiz_id")
        private Quiz quiz;

        @Enumerated(EnumType.STRING)
        @Type(value = ContentTypeConverter.class)
        private ContentType type;

        @Column(name = "sequence_number")
        private Integer sequenceNumber;
    }
