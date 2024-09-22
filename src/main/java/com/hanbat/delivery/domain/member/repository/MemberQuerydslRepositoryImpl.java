package com.hanbat.delivery.domain.member.repository;

import static com.hanbat.delivery.domain.member.entity.QMember.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hanbat.delivery.domain.member.entity.Major;
import com.hanbat.delivery.domain.member.entity.Member;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MemberQuerydslRepositoryImpl implements MemberQuerydslRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Member> findMemberByMajorAndSearch(String major, String search) {
		return queryFactory
			.selectFrom(member)
			.where(addSearchAndMajorFilter(major, search))
			.fetch();
	}

	private BooleanExpression addSearchAndMajorFilter(String major, String search) {
		return addMajorFilter(major).and(addSearchFilter(search));
	}

	private BooleanExpression addSearchFilter(String search) {
		return StringUtils.hasText(search) ? member.name.contains(search) : Expressions.TRUE;
	}

	private BooleanExpression addMajorFilter(String major){
		return major != null ? member.major.eq(Major.fromValue(major)) : Expressions.TRUE;
	}

}
