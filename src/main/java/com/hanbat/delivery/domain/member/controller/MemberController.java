package com.hanbat.delivery.domain.member.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.hanbat.delivery.domain.member.dto.MemberInfoResponse;
import com.hanbat.delivery.domain.member.dto.ProfAndTAWithCountResponse;
import com.hanbat.delivery.domain.member.entity.Major;
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

	@GetMapping("/search")
	public ResponseEntity<List<MemberInfoResponse>> getMemberBySearch(@RequestParam(name = "value", required = false) String major, @RequestParam(name = "search", required = false) String search) {
		log.info("Received major: {}", major);
		log.info("Received search: {}", search);

		return ResponseEntity.ok(memberService.getMembersBySearchAndMajor(major, search));
	}
}
