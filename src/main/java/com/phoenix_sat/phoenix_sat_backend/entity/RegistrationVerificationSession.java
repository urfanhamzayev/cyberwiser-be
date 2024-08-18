package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "registration_verification_sessions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@SQLRestriction("is_deleted = false")
public class RegistrationVerificationSession extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "verification_id")
    private String verificationId;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
}
