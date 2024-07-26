package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.converter.RoleTypeConverter;
import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(generator = "idGenerator")
    @GenericGenerator(name = "idGenerator", type = IdGenerator.class)
    private String id;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @Column(name = "organization_id", insertable = false, updatable = false)
    private String organizationId;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns ={@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns ={@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles;

    @Column(name = "picture_url")
    private String pictureUrl;

    private String name;
    private String email;
    private String password;


    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = false;
}
