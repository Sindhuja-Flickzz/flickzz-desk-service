package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.flickzz.desk.model.EnquiryRegistration;

public interface EnquiryRegistrationRepository extends JpaRepository<EnquiryRegistration, Long> {

	@Query("SELECT e FROM EnquiryRegistration e WHERE e.email = :email AND e.isActive = :active")
	Optional<EnquiryRegistration> findByEmailAndIsActive(String email, Boolean active);

	boolean existsByUserName(String username);

	Optional<EnquiryRegistration> findByUserNameAndIsActive(String username, Boolean active);

	Optional<EnquiryRegistration> findByEmail(String email);

	Optional<EnquiryRegistration> findByEnquiryIdAndIsActive(String enquiryId, Boolean active);

}
