package com.flickzz.desk.security;

import static org.springframework.http.HttpHeaders.*;
import static org.springframework.http.HttpMethod.*;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.*;
import org.springframework.web.cors.*;
import org.springframework.web.filter.*;

import com.flickz.desk.handler.*;
import com.flickzz.desk.repo.*;

@Configuration
public class SecurityConfig {

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Bean
	FlickzzDeskLogoutSuccessHandler flickzzDeskLogoutSuccessHandler(AuthRepository repo) {
		return new FlickzzDeskLogoutSuccessHandler(repo);
	}

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, FlickzzDeskLogoutSuccessHandler logoutSuccessHandler)
			throws Exception {
		http.cors(Customizer.withDefaults()).csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
						auth -> auth
								.requestMatchers("/register", "/verify", "/login", "/auth/**", "/refresh", "/reset/**",
										"/country/**", "/enquiry/**", "/user/**")
								.permitAll().anyRequest().authenticated())
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
				.logout(logout -> logout.logoutUrl("/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID")
						.logoutSuccessHandler(logoutSuccessHandler));

		return http.build();
	}

	@Bean
	AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOrigins(
				List.of("http://localhost:4200", "https://flickzz-desk-service-production.up.railway.app",
						"https://flickzz-desk-ui-production.up.railway.app"));
		config.setAllowedHeaders(Arrays.asList(ORIGIN, CONTENT_TYPE, ACCEPT, AUTHORIZATION, "x-user-id"));
		config.setAllowedMethods(Arrays.asList(GET.name(), POST.name(), DELETE.name(), PUT.name(), PATCH.name()));
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);

	}
}
