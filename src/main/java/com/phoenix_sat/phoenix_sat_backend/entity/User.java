package com.phoenix_sat.phoenix_sat_backend.entity;

import com.phoenix_sat.phoenix_sat_backend.entity.generator.IdGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.ParamDef;

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
@FilterDef(
        name = "userFilter",
        parameters = {
                @ParamDef(name = "isDeleted", type = Boolean.class),
                @ParamDef(name = "isActive", type = Boolean.class)
        }
)
@Filter(
        name = "userFilter",
        condition = "is_deleted = :isDeleted AND is_active = :isActive"
)
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
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")}
    )
    private Set<Role> roles;

    @Column(name = "profile_picture_key")
    private String profilePictureKey;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;
    private String password;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    public String getOrganizationId() {
        return this.organization.getId();
    }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

}
