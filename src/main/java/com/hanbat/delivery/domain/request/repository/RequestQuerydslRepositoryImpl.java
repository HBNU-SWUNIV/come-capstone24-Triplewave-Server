package com.hanbat.delivery.domain.request.repository;

import org.springframework.stereotype.Repository;

import static com.hanbat.delivery.domain.request.entity.QRequest.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hanbat.delivery.domain.request.entity.Request;
import com.hanbat.delivery.domain.request.entity.RequestStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RequestQuerydslRepositoryImpl implements RequestQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<List<Request>> findCompletedRequestsByDateRange(String dateRange) {
		return Optional.ofNullable(queryFactory
			.selectFrom(request)
			.where(addDateFilter(dateRange),
				request.status.eq(RequestStatus.COMPLETED))
			.orderBy(request.statusTime.desc())
			.fetch());
	}

	// 기간별 필터링
	private BooleanExpression addDateFilter(String dateRange) {
		if (dateRange != null) {
			switch (dateRange) {
				case "1주" -> {
					return request.statusTime.between(
						LocalDateTime.now().minusDays(7),
						LocalDateTime.now()
					);
				}
				case "1개월" -> {
					return request.statusTime.between(
						LocalDateTime.now().minusMonths(1),
						LocalDateTime.now()
					);
				}
				case "2개월" -> {
					return request.statusTime.between(
						LocalDateTime.now().minusMonths(2),
						LocalDateTime.now()
					);
				}
			}

		}
		return Expressions.asBoolean(true).isTrue();
	}
}
