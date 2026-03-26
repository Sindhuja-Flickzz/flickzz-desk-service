package com.flickzz.desk.security;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.ORIGIN;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.flickz.desk.handler.FlickzzDeskLogoutSuccessHandler;
import com.flickzz.desk.repo.AuthRepository;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private JwtRequestFilter jwtRequestFilter;

    @Bean
    FlickzzDeskLogoutSuccessHandler flickzzDeskLogoutSuccessHandler(AuthRepository repo) {
	    return new FlickzzDeskLogoutSuccessHandler(repo);
	}

	@Bean
    SecurityFilterChain filterChain(HttpSecurity http, FlickzzDeskLogoutSuccessHandler logoutSuccessHandler) throws Exception {
        http
        	.cors(Customizer.withDefaults())
        	.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/register","/verify","/login","/auth/**", "/refresh","/menu/**").permitAll()
	            .anyRequest().authenticated()
	        )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
            .logout(logout -> logout
              .logoutUrl("/logout")
              .invalidateHttpSession(true)
              .deleteCookies("JSESSIONID")
              .logoutSuccessHandler(logoutSuccessHandler)
            );

        return http.build();
    }
	
    @Bean
    AuthenticationManager authManager(AuthenticationConfiguration  config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsFilter corsFilter() {
      final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      final CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
      config.setAllowedHeaders(Arrays.asList(
              ORIGIN,
              CONTENT_TYPE,
              ACCEPT,
              AUTHORIZATION,
              "x-user-id"
      ));
      config.setAllowedMethods(Arrays.asList(
              GET.name(),
              POST.name(),
              DELETE.name(),
              PUT.name(),
              PATCH.name()
      ));
      source.registerCorsConfiguration("/**", config);
      return new CorsFilter(source);

    }
}

