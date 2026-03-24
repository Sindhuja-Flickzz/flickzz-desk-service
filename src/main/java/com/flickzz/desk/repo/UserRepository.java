package com.flickzz.desk.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String username);

}
