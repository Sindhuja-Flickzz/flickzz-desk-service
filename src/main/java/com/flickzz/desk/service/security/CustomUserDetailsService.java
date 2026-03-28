package com.flickzz.desk.service.security;

import static com.flickzz.desk.config.FlickzzDeskConstants.FD_USER;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // your DB repo

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
        		.orElseThrow(()-> new FlickzzDeskException(DOES_NOT_EXIST, getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));

        return new CustomUserDetails(
                user.getUserName(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
        );
    }
}
