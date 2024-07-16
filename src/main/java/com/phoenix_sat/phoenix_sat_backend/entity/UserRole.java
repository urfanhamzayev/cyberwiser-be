package com.phoenix_sat.phoenix_sat_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserRole {
    @EmbeddedId
    private UserRoleKey id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @MapsId(value = "roleId")
    private Role role;

    @ManyToOne
    @MapsId(value = "userId")
    @JoinColumn(name = "user_id")
    private User user;
}
