package com.hanbat.delivery.domain.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hanbat.delivery.domain.member.dto.MemberInfoResponse;
import com.hanbat.delivery.domain.member.dto.ProfAndTAWithCountResponse;
import com.hanbat.delivery.domain.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {
	private final MemberService memberService;

	@GetMapping("/info")
	public ResponseEntity<MemberInfoResponse> getMemberInfo() {
		return ResponseEntity.ok(memberService.getMemberInfo());
	}

	@GetMapping("/prof/info")
	public ResponseEntity<ProfAndTAWithCountResponse> getProfAndTA() {
		return ResponseEntity.ok(memberService.getProfAndTAList());
	}
}
