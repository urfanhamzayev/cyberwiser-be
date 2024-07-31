package com.phoenix_sat.phoenix_sat_backend.batch;

import com.phoenix_sat.phoenix_sat_backend.enums.RoleType;
import com.phoenix_sat.phoenix_sat_backend.model.request.UserRequest;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.List;

public class UserFieldMapper implements FieldSetMapper<UserRequest> {

    private final String organizationId;

    public UserFieldMapper(String organizationId) {
        this.organizationId = organizationId;
    }

    @Override
    public UserRequest mapFieldSet(FieldSet fieldSet) throws BindException {
        String[] fullName = fieldSet.readString("fullName").split(" ");
        String firstName = fullName[0];
        String lastName =fullName[1];
        return UserRequest.builder()
                .organizationId(organizationId)
                .firstName(firstName)
                .lastName(lastName)
                .email(fieldSet.readString("email"))
                .roleType(getRole(fieldSet.readString("role")))
                .build();
    }


    private List<RoleType> getRole(String roleTypeParam) {
        List<RoleType> roleTypeList =new ArrayList<>();
        String[] roleTypes = roleTypeParam.split("&");
        for (String roleType : roleTypes) {
            if (roleType.equalsIgnoreCase(RoleType.SUPER_ADMIN.name()))
                roleTypeList.add(RoleType.SUPER_ADMIN);
            else if (roleType.equalsIgnoreCase(RoleType.ADMIN.name()))
                roleTypeList.add(RoleType.ADMIN);
            else
                roleTypeList.add(RoleType.SUPER_ADMIN);
        }
        return roleTypeList;

    }
}
