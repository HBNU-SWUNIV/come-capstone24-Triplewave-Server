package com.hanbat.delivery.domain.member.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfAndTAWithCountResponse {
	private long count;
	private List<ProfAndTAResponse> profAndTAResponses;

	@Builder
	public ProfAndTAWithCountResponse(long count, List<ProfAndTAResponse> profAndTAResponses) {
		this.count = count;
		this.profAndTAResponses = profAndTAResponses;
	}
}
