package com.phoenix_sat.phoenix_sat_backend.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix_sat.phoenix_sat_backend.constant.SecurityConstants;
import com.phoenix_sat.phoenix_sat_backend.error.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private final SecurityConstants securityConstants;
    private final JWTProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.isNotBlank(authHeader) && authHeader.startsWith("Bearer ")) {
            String jwtToken = authHeader.substring(7);
            if (StringUtils.isNotBlank(jwtToken)) {
                try {
                    String userId = jwtProvider.extractUserId(jwtToken);
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);
                    if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                } catch (JWTVerificationException ex) {
                    ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, "Provide valid Basic authentication header");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
                    return;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bad bearer authentication");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
