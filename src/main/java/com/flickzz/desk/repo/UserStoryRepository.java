package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flickzz.desk.model.UserStory;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, Long> {

}
