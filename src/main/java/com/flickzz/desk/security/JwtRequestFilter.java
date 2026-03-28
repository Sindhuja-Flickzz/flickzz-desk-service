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

    	try {
	        final String authHeader = request.getHeader("Authorization");
	
	        String username = null;
	        String jwt = null;
	
	        // Extract JWT from Authorization header
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            jwt = authHeader.substring(7);
	            try {
	                username = jwtUtil.extractUsername(jwt);
	            } catch (ExpiredJwtException e) {
	            	log.warn("JWT expired: {}", e.getMessage());
	            } catch (Exception e) {
	            	log.error("JWT parsing error: {}", e.getMessage());
	            }
	        }
	
	        // Validate token and set authentication
	        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	        	CustomUserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
	
	            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
	                var authToken = new UsernamePasswordAuthenticationToken(
	                		userDetails, null, userDetails.getAuthorities());
	                SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }
	
	        chain.doFilter(request, response);
    	} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"JWT expired\"}");
		}
    }
}
