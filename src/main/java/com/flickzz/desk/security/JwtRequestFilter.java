package com.flickzz.desk.security;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flickzz.desk.service.security.CustomUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private static final Logger log = LoggerFactory.getLogger(JwtRequestFilter.class);

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extract JWT from Authorization header
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                log.warn("JWT token has expired for token: {}", jwt.substring(0, Math.min(20, jwt.length())) + "...");
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token has expired");
                return;
            } catch (JwtException e) {
                log.error("JWT parsing error: {}", e.getMessage());
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid or malformed JWT token");
                return;
            } catch (Exception e) {
                log.error("Unexpected error during JWT extraction: {}", e.getMessage());
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication error");
                return;
            }
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JWT validation failed for user: {}", username);
                    sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "JWT token validation failed");
                    return;
                }
            } catch (Exception e) {
                log.error("Error validating JWT for user {}: {}", username, e.getMessage());
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "Authentication error: " + e.getMessage());
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }
}
