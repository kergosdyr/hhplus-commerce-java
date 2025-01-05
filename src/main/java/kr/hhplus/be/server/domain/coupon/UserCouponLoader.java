package kr.hhplus.be.server.domain.coupon;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponLoader {

	private final UserCouponRepository userCouponRepository;

	public List<UserCoupon> loadAllByUserId(long userId) {
		return userCouponRepository.findAllByUserId(userId);
	}
}
