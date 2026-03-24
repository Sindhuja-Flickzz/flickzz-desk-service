package com.flickzz.desk.service;

import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	public UserDetails loadUserByUsername(String username) {
		// Fetch user from DB
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,getDescription(DOES_NOT_EXIST.getDescription(), username)));

        // Convert to Spring Security UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserName())
                .password(user.getPassword()) // must be encoded
                .authorities(user.getRole())
//                		.stream()
//                        .map(role -> new SimpleGrantedAuthority(role.getName()))
//                        .toList())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
	}
}
