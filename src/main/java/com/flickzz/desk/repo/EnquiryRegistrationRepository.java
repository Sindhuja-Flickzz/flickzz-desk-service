package com.flickzz.desk.repo;

import com.flickzz.desk.model.EnquiryRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnquiryRegistrationRepository extends JpaRepository<EnquiryRegistration, Long> {


    Optional<EnquiryRegistration> findTopByEmailAndIsActiveTrueOrderByVersionDesc(String email);

    Optional<EnquiryRegistration> findByUserNameAndIsActiveTrue(String username);

    Optional<EnquiryRegistration> findByEmail(String email);

    Optional<EnquiryRegistration> findByEnquiryIdAndIsActiveTrue(String enquiryId);

    Optional<EnquiryRegistration> findTopByEmailAndCompany_CompanyIdAndIsActiveTrueOrderByVersionDesc(String email, String companyId);

}
