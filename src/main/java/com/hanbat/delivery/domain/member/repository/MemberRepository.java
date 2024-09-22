package com.hanbat.delivery.domain.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hanbat.delivery.domain.member.entity.Major;
import com.hanbat.delivery.domain.member.entity.Member;
import com.hanbat.delivery.domain.member.entity.Role;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQuerydslRepository {
	@Query("SELECT m FROM Member m WHERE m.major = :major AND m.role IN (:roles)")
	Optional<List<Member>> findProfAndTA(@Param("major")Major major, @Param("roles") List<Role> roles);

	@Query("SELECT COUNT(m) FROM Member m WHERE m.major = :major AND m.role IN (:roles)")
	long countProfAndTA(@Param("major") Major major, @Param("roles") List<Role> roles);
}
