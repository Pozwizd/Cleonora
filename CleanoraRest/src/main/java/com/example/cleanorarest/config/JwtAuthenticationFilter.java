package com.example.cleanorarest.config;


import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.cleanorarest.service.JwtService;

import java.io.IOException;
import java.io.OutputStream;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String requestUri = request.getRequestURI();
        if (authHeader == null
                && !requestUri.equals("/")
                && !requestUri.contains("/swagger-ui")
                && !requestUri.contains("/v3/api-docs")
                && !requestUri.contains("/api/v1/register")
                && !requestUri.contains("/api/v1/login")
                && !requestUri.contains("/api/v1/refreshToken")
                && !requestUri.contains("/api/v1/forgotPassword")
                && !requestUri.contains("/api/v1/changePassword")) {
            onFailedAuthentication(request, response);
            return;
        }
        String jwt;
        String email;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        try {
            email = jwtService.extractCustomerEmail(jwt);
        } catch (ExpiredJwtException ex) {
            onFailedAuthentication(request, response);
            return;
        }
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    protected void onFailedAuthentication(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        try (OutputStream out = response.getOutputStream()) {
            out.flush();
        } catch (IOException e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}