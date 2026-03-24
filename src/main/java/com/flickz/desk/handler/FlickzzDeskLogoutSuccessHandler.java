package com.flickz.desk.handler;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import com.flickzz.desk.repo.AuthRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class FlickzzDeskLogoutSuccessHandler implements LogoutSuccessHandler {

    private final AuthRepository authRepository;

    public FlickzzDeskLogoutSuccessHandler(AuthRepository repo) {
		this.authRepository = repo;
	}

	@SuppressWarnings("deprecation")
	@Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                org.springframework.security.core.Authentication authentication) throws IOException {
        
        String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(body);
        String refreshToken = jsonNode.get("refreshToken").asText();
        
        if (refreshToken != null) {
        	authRepository.findByToken(refreshToken).ifPresent(authRepository::delete);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write("{\"message\":\"Logout successful\"}");
        response.getWriter().flush();
    }
}
