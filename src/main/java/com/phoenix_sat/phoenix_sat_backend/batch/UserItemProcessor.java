package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.entity.Role;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRequest;
import com.phoenix_sat.phoenix_sat_backend.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class UserItemProcessor implements ItemProcessor<UserRequest, User> {

    private final RoleRepository roleRepository;
    private List<Role> defaultRoles;

    @PostConstruct
    public void init() {
        defaultRoles = roleRepository.findAll();
    }

    @Override
    public User process(UserRequest userRow) throws Exception {
//        System.out.println("User's organization id is " + userRow.organizationId());
        return User.builder()
                .email(userRow.email())
                .roles(defaultRoles.stream()
                        .filter(role -> userRow.roleType().contains(role.getRole()))
                        .collect(Collectors.toSet())
                )
                .organizationId(userRow.organizationId())
                .firstName(userRow.firstName())
                .lastName(userRow.lastName())
                .build();
    }

}
