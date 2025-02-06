package kr.hhplus.be.server.infra.storage.coupon;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RKeys;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysScanOptions;
import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.enums.RedisKeyPrefix;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserCouponRedissonRepository {

	private final RedissonClient redissonClient;

	private static LocalDateTime fromEpochMillis(double epochMillis) {
		return LocalDateTime.ofEpochSecond((long)epochMillis / 1000,
			(int)((epochMillis % 1000) * 1_000_000),
			ZoneOffset.UTC);
	}

	public boolean save(UserCoupon userCoupon) {
		RSet<Long> userCouponSet = redissonClient.getSet(RedisKeyPrefix.ISSUED.getPrefix() + userCoupon.getCouponId());
		return userCouponSet.add(userCoupon.getUserId());
	}

	public boolean existsByUserIdAndCouponId(long userId, long couponId) {
		RSet<Long> userCouponSet = redissonClient.getSet(RedisKeyPrefix.ISSUED.getPrefix() + couponId);
		return userCouponSet.contains(userId);
	}

	public List<UserCouponWaitOutput> findAllWait() {
		KeysScanOptions keysScanOptions = KeysScanOptions.defaults()
			.pattern(RedisKeyPrefix.ISSUE_WAIT.getAllKeysPattern());

		RKeys keys = redissonClient.getKeys();
		return keys.getKeysStream(keysScanOptions)
			.flatMap(name -> {
				String couponIdString = StringUtils.split(name, ":")[3];
				long couponId = Long.parseLong(couponIdString);

				RScoredSortedSet<Long> scoredSortedSet = redissonClient.getScoredSortedSet(name);

				return scoredSortedSet.entryRangeReversed(0, 100).stream()
					.map(entry -> new UserCouponWaitOutput(
						entry.getValue(),
						couponId,
						fromEpochMillis(entry.getScore())
					));
			})
			.toList();

	}

	public boolean publishCouponIssuedWait(long userId, long couponId, LocalDateTime issuedAt) {

		RScoredSortedSet<Long> couponIssueSet = redissonClient.getScoredSortedSet(
			RedisKeyPrefix.ISSUE_WAIT.getPrefix() + couponId);
		return couponIssueSet.addIfAbsent(issuedAt.toInstant(ZoneOffset.UTC).toEpochMilli(), userId);

	}

}
