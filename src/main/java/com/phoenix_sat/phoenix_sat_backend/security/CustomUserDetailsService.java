package com.phoenix_sat.phoenix_sat_backend.security;

import com.phoenix_sat.phoenix_sat_backend.entity.User;
import com.phoenix_sat.phoenix_sat_backend.error.exception.ResourceNotFoundException;
import com.phoenix_sat.phoenix_sat_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User not found with this id: " + id));

        return new org.springframework.security.core.userdetails.User(
                user.getId(),
                user.getPassword(),
                user.getRoles().stream().map(it-> new SimpleGrantedAuthority(it.getRole().name())).collect(Collectors.toList()) // Adjust authorities as needed
        );
    }
}
