package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.infra.storage.coupon.UserCouponWaitOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCouponIssueScheduler {

	private final UserCouponRepository userCouponRepository;

	private final CouponReader couponReader;

	private final CouponRepository couponRepository;

	private final UserCouponValidator userCouponValidator;

	@Scheduled(timeUnit = TimeUnit.MINUTES, fixedDelay = 1)
	@Transactional
	public void issueAllCouponWait() {

		List<UserCouponWaitOutput> userCouponWaitOutputs = userCouponRepository.findAllWait();

		userCouponWaitOutputs
			.forEach(couponWaitOutput -> {

				try {
					userCouponValidator.validate(couponWaitOutput.userId(), couponWaitOutput.couponId());

					Coupon coupon = couponReader.readIssuable(couponWaitOutput.couponId(), couponWaitOutput.issuedAt());

					couponRepository.issue(couponWaitOutput.couponId());

					UserCoupon userCoupon = UserCoupon.fromCoupon(coupon, couponWaitOutput.userId(),
						couponWaitOutput.issuedAt());

					userCouponRepository.save(userCoupon);
					//TODO : 추후 MQ 로 변경 시 응답 로직을 대체하거나 비동기 통신으로 고객에게 알림 추가 필요

				} catch (Exception e) {
					//TODO : 추후 MQ 로 변경 시 응답 로직을 대체하거나 비동기 통신으로 고객에게 알림 추가 필요
					log.error(e.getMessage());
				}

			});
	}

}
