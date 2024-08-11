package com.hanbat.delivery.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hanbat.delivery.domain.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
