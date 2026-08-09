package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.EnquiryRegistration;

public interface EnquiryRegistrationRepository extends JpaRepository<EnquiryRegistration, Long> {


	Optional<EnquiryRegistration> findTopByEmailAndIsActiveTrueOrderByVersionDesc(String email);

	Optional<EnquiryRegistration> findByUserNameAndIsActiveTrue(String username);

	Optional<EnquiryRegistration> findByEmailAndIsActiveTrue(String email);

	Optional<EnquiryRegistration> findByEnquiryIdAndIsActiveTrue(String enquiryId);

	Optional<EnquiryRegistration> findTopByEmailAndCompany_CompanyIdAndIsActiveTrueOrderByVersionDesc(String email, String companyId);

}
