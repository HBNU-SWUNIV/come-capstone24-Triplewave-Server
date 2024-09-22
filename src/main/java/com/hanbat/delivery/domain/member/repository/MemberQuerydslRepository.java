package com.hanbat.delivery.domain.member.repository;

import java.util.List;
import com.hanbat.delivery.domain.member.entity.Member;

public interface MemberQuerydslRepository {
	List<Member> findMemberByMajorAndSearch(String major, String search);
}
