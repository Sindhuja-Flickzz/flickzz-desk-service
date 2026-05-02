package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.EnquiryInfo;

public interface EnquiryInfoRepository extends JpaRepository<EnquiryInfo, Long> {

	Optional<EnquiryInfo> findByToken(String token);

}
