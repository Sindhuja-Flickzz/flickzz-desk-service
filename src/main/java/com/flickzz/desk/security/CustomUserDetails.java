package com.flickzz.desk.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(String username, String password, String firstName,
                             String lastName, String email, String role, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
