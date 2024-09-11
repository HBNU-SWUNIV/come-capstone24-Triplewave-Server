package com.hanbat.delivery.domain.request.repository;

import org.springframework.stereotype.Repository;

import static com.hanbat.delivery.domain.request.entity.QRequest.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hanbat.delivery.domain.request.entity.Request;
import com.hanbat.delivery.domain.request.entity.RequestStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
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

		JPAQuery<Request> query = queryFactory
			.selectFrom(request)
			.where(request.status.eq(RequestStatus.COMPLETED));

		if (dateRange != null) {
			BooleanExpression dateFilter = addDateFilter(dateRange);
			if (dateFilter != null) {
				query.where(dateFilter);
			} else {
				return Optional.empty();
			}
		}
		return Optional.ofNullable(query
			.orderBy(request.statusTime.desc())
			.fetch());
	}

	// 기간별 필터링
	private BooleanExpression addDateFilter(String dateRange) {

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

			default -> {
				return null;
			}
		}
	}
}
