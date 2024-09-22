package com.hanbat.delivery.domain.request.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanbat.delivery.domain.request.entity.Request;

public interface RequestRepository extends JpaRepository<Request, Long>, RequestQuerydslRepository {
	@Query("SELECT r FROM Request r WHERE r.statusTime >= :startOfDay AND r.statusTime < :endOfDay AND r.receiver.id = :userid")
	Optional<List<Request>> findRequestsByDateAndReceiver(@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay,
		@Param("userid") Long userid);

	@Query("SELECT r FROM Request r WHERE r.statusTime >= :startOfDay AND r.statusTime < :endOfDay AND r.requester.id = :userid")
	Optional<List<Request>> findRequestsByDateAndRequester(@Param("startOfDay") LocalDateTime startOfDay,
		@Param("endOfDay") LocalDateTime endOfDay,
		@Param("userid") Long userid);

}
