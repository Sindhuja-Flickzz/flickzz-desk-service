package com.flickzz.desk.service.security;

import static com.flickzz.desk.config.FlickzzDeskConstants.ACTIVE;
import static com.flickzz.desk.config.FlickzzDeskConstants.FD_USER;
import static com.flickzz.desk.config.FlickzzDeskUtility.getDescription;
import static com.flickzz.desk.exception.FlickzzDeskErrorCodes.DOES_NOT_EXIST;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.flickzz.desk.exception.FlickzzDeskException;
import com.flickzz.desk.model.EnquiryRegistration;
import com.flickzz.desk.model.User;
import com.flickzz.desk.repo.EnquiryRegistrationRepository;
import com.flickzz.desk.repo.UserRepository;
import com.flickzz.desk.security.CustomUserDetails;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository; // your DB repo

	@Autowired
	private EnquiryRegistrationRepository enquiryRegistrationRepository;

	@Override
	public CustomUserDetails loadUserByUsername(String username) {
		EnquiryRegistration enquiryRegistration = enquiryRegistrationRepository
				.findByUserNameAndIsActive(username, ACTIVE).orElse(null);

		if (enquiryRegistration != null) {
			return new CustomUserDetails(enquiryRegistration.getUserName(), enquiryRegistration.getPassword(),
					enquiryRegistration.getFirstName(), enquiryRegistration.getLastName(),
					enquiryRegistration.getEmail(), enquiryRegistration.getUserRole(),
					Collections.singletonList(new SimpleGrantedAuthority(enquiryRegistration.getUserRole())));
		}

		User user = userRepository.findByUserName(username).orElseThrow(() -> new FlickzzDeskException(DOES_NOT_EXIST,
				getDescription(DOES_NOT_EXIST.getDescription(), FD_USER)));

		return new CustomUserDetails(user.getUserName(), user.getPassword(), user.getFirstName(), user.getLastName(),
				user.getEmail(), user.getRole(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole())));
	}
}
