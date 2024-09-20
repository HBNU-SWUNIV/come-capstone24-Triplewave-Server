package com.hanbat.delivery.domain.member.dto;

import com.hanbat.delivery.domain.member.entity.Building;
import com.hanbat.delivery.domain.member.entity.Role;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfAndTAResponse {
	private Long memberId;
	private String name;
	private Role role;
	private Building building;

	@Builder
	public ProfAndTAResponse(Long memberId, String name, Role role, Building building) {
		this.memberId = memberId;
		this.name = name;
		this.role = role;
		this.building = building;
	}
}
