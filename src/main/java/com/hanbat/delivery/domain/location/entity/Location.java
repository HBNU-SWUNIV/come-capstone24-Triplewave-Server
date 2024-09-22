package com.hanbat.delivery.domain.location.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Location {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "BIGINT(11)")
	private Long id;

	@Column(columnDefinition = "CHAR(10)", nullable = false)
	private String name;

	@Column(name = "position_x", columnDefinition = "DOUBLE", nullable = false)
	private Double positionX;

	@Column(name = "position_y", columnDefinition = "DOUBLE", nullable = false)
	private Double positionY;

	@Column(name = "position_z",columnDefinition = "DOUBLE", nullable = false)
	private Double positionZ;

	@Column(name = "orientation_x",columnDefinition = "DOUBLE", nullable = false)
	private Double orientationX;

	@Column(name = "orientation_y",columnDefinition = "DOUBLE", nullable = false)
	private Double orientationY;

	@Column(name = "orientation_z",columnDefinition = "DOUBLE", nullable = false)
	private Double orientationZ;

	@Column(name = "orientation_w",columnDefinition = "DOUBLE", nullable = false)
	private Double orientationW;


}
