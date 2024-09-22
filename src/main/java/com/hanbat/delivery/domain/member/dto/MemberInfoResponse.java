package com.hanbat.delivery.domain.member.dto;

import com.hanbat.delivery.domain.member.entity.Building;
import com.hanbat.delivery.domain.member.entity.Major;
import com.hanbat.delivery.domain.member.entity.Member;
import com.hanbat.delivery.domain.member.entity.Role;

public record MemberInfoResponse(Long memberId, String name, Role role, Building building,
								 Major major) {
	public static MemberInfoResponse fromMember(Member member) {
		return new MemberInfoResponse(member.getId(), member.getName(), member.getRole(), member.getBuilding(), member.getMajor());
	}
}
