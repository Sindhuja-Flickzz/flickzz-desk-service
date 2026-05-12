package com.flickzz.desk.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flickzz.desk.model.RequestItem;

public interface RequestItemRepository extends JpaRepository<RequestItem, Long> {

}
