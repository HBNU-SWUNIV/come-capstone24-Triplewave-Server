package com.hanbat.delivery.domain.location.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hanbat.delivery.domain.location.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {

	@Query("SELECT l FROM Location l WHERE l.name = :name")
	Optional<Location> findByName(@Param("name") String name);
}
