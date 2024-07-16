package com.phoenix_sat.phoenix_sat_backend.model.response;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    private User user;
}
