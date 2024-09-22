package com.hanbat.delivery.domain.request.repository;

import java.util.List;
import java.util.Optional;

import com.hanbat.delivery.domain.request.entity.Request;

public interface RequestQuerydslRepository {
	Optional<List<Request>> findCompletedRequestsByDateRange(String dateRange);
}
