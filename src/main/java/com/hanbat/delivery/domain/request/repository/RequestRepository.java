package com.hanbat.delivery.domain.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanbat.delivery.domain.request.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
}
