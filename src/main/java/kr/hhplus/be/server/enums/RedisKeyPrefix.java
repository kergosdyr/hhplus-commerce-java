package kr.hhplus.be.server.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RedisKeyPrefix {

	ISSUE_WAIT("coupon:issue:coupon_id:"), ISSUED("issued:user_coupon:coupon_id:"), COUPON("coupon:coupon_id:"),
	;

	private final String prefix;

	public String getAllKeysPattern() {
		return prefix + "*";
	}

	public String getKey(long id) {
		return prefix + id;
	}

}
