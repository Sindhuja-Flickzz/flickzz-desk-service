package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.LoginMaster;

public interface LoginMasterRepository extends JpaRepository<LoginMaster, Long> {

	Optional<LoginMaster> findByUserName(String username);

}
