package com.hanbat.delivery.domain.request.entity;

import java.time.LocalDateTime;

import com.hanbat.delivery.domain.location.entity.Location;
import com.hanbat.delivery.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Request {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "BIGINT(11)")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requester_id", nullable = false)
	private Member requester;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requestee_id", nullable = false)
	private Member requestee;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "destination_id", nullable = false)
	private Location destination;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "departure_id")
	private Location departure;

	@Column(columnDefinition = "VARCHAR(30)", nullable = false)
	private String stuff;

	@Column(columnDefinition = "VARCHAR(255)", nullable = false)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private RequestStatus status;

	@Column(name = "status_time", columnDefinition = "DATETIME", nullable = false)
	private LocalDateTime statusTime;



}
