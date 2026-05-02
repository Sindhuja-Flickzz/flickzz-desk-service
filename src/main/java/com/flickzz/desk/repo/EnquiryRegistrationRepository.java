package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.EnquiryRegistration;

public interface EnquiryRegistrationRepository extends JpaRepository<EnquiryRegistration, Long> {

	Optional<EnquiryRegistration> findByEmailAndIsActive(String email, Boolean active);

	boolean existsByUserName(String username);

	Optional<EnquiryRegistration> findByUserNameAndIsActive(String username, Boolean active);

}
