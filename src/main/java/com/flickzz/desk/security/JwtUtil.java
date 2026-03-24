package com.flickzz.desk.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;

@Component
public class JwtUtil {
	
    @Value("${jwt.secret}")
    private String secret;

	@Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    @SuppressWarnings("deprecation")
	public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS256, secret.getBytes())
            .compact();
    }

    @SuppressWarnings("deprecation")
	public String extractUsername(String token) {
        return Jwts.parser()
        		.setSigningKey(secret.getBytes())
        		.parseClaimsJws(token)
        		.getBody()
        		.getSubject();
    }

    public boolean validateToken(String token,  String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        @SuppressWarnings("deprecation")
		Date expiration = Jwts.parser()
        		.setSigningKey(secret.getBytes())
        		.parseClaimsJws(token)
        		.getBody()
        		.getExpiration();
        return expiration.before(new Date());
    }
}
