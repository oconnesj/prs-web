package com.prs.db;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prs.business.Request;

public interface RequestRepository extends JpaRepository<Request, Integer> {
	List<Request> findByStatusAndUserIdNot(String status, int id);




}
