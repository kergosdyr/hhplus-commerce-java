package kr.hhplus.be.server.api.request;

import java.time.LocalDateTime;

public record CouponIssueRequest(long userId, long couponId, LocalDateTime issuedAt) {
}
