package com.hanbat.delivery.domain.member.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hanbat.delivery.domain.member.dto.MemberInfoResponse;
import com.hanbat.delivery.domain.member.dto.ProfAndTAResponse;
import com.hanbat.delivery.domain.member.dto.ProfAndTAWithCountResponse;
import com.hanbat.delivery.domain.member.entity.Major;
import com.hanbat.delivery.domain.member.entity.Member;
import com.hanbat.delivery.domain.member.entity.Role;
import com.hanbat.delivery.domain.member.repository.MemberRepository;
import com.hanbat.delivery.global.error.exception.CustomException;
import com.hanbat.delivery.global.error.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
	private final MemberRepository memberRepository;

	// 멤버 조회
	public MemberInfoResponse getMemberInfo() {
		Long memberId = 1L;
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		return MemberInfoResponse.fromMember(member);
	}

	// 해당 멤버와 같은 학과인 교수님, 조교님들 멤버리스트 조회
	public ProfAndTAWithCountResponse getProfAndTAList() {
		Long memberId = 1L;
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

		List<Role> roles = new ArrayList<>();
		roles.add(Role.ASSISTANT);
		roles.add(Role.PROFESSOR);

		List<Member> members = memberRepository.findProfAndTA(member.getMajor(), roles)
			.orElseThrow(() -> new CustomException(ErrorCode.PROF_TA_MEMBER_NOT_FOUND));

		long count = memberRepository.countProfAndTA(member.getMajor(), roles);

		List<ProfAndTAResponse> profAndTAResponses = new ArrayList<>();
		for (Member m : members) {
			profAndTAResponses.add(ProfAndTAResponse.builder()
				.memberId(m.getId())
				.name(m.getName())
				.role(m.getRole())
				.building(m.getBuilding())
				.build());
		}

		return ProfAndTAWithCountResponse.builder()
			.count(count)
			.profAndTAResponses(profAndTAResponses)
			.build();

	}

	// 이름과 학과 검색 시 다른 사람들 조회
	public List<MemberInfoResponse> getMembersBySearchAndMajor(String major, String search) {
		List<Member> members = memberRepository.findMemberByMajorAndSearch(major, search);

		List<MemberInfoResponse> memberInfoResponses = new ArrayList<>();
		for (Member m : members) {
			memberInfoResponses.add(MemberInfoResponse.fromMember(m));
		}
		return memberInfoResponses;
	}

}
