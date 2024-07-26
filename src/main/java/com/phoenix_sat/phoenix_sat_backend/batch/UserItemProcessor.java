package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.entity.Organization;
import com.phoenix_sat.phoenix_sat_backend.entity.Role;
import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRequest;
import com.phoenix_sat.phoenix_sat_backend.repository.OrganizationRepository;
import com.phoenix_sat.phoenix_sat_backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserItemProcessor implements ItemProcessor<UserRequest, User> {

    private final RoleRepository roleRepository;
    private final OrganizationRepository organizationRepository;


    @Override
    public User process(UserRequest item) throws Exception {
        return User.builder()
                .email(item.email())
                .roles(getRolesByRoleTypes(item.roleType()))
                .organization(getOrganizationById(item.organizationId()))
                .name(item.name())
                .build();
    }

    private Organization getOrganizationById(String orgId) {
        return organizationRepository.findById(orgId).orElseThrow(() ->
                new ResourceNotFoundException("Organization not found with this id:" + orgId));
    }

    private Set<Role> getRolesByRoleTypes(List<RoleType> roleTypeList) {
        Set<Role> roles = new HashSet<>();
        for (RoleType roleType : roleTypeList) {
            Role role = roleRepository.findByRole(roleType).orElseThrow(() ->
                    new ResourceNotFoundException("Role not found with this roleType:" + roleType));
            roles.add(role);

        }
        return roles;
    }
}
