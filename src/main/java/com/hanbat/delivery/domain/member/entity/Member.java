package com.hanbat.delivery.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "BIGINT(11)")
	private Long id;

	@Column(columnDefinition = "VARCHAR(20)", nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Building building;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Major major;

	@Column(name = "school_id", columnDefinition = "VARCHAR(255)", nullable = false)
	private String schoolId;

	@Builder
	public Member(String name, Building building, Major major, String schoolId) {
		this.name = name;
		this.building = building;
		this.major = major;
		this.schoolId = schoolId;
	}
}
