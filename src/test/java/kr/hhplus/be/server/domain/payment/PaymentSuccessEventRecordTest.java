package kr.hhplus.be.server.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.enums.PaymentEventRecordStatus;

class PaymentSuccessEventRecordTest {

	@Test
	@DisplayName("PaymentSuccessEventRecord 의 success 를 호출하는 경우, 상태를 SENT 로 변경하고 sentAt 을 주어진 값으로 변경한다.")
	void shouldStatusSentAndSentAtLocalDateTimeWhenCalledSuccess() {
		//given
		PaymentSuccessEventRecord record = PaymentSuccessEventRecord.builder()
			.status(PaymentEventRecordStatus.FAILED)
			.sentAt(LocalDateTime.of(2025, 1, 1, 0, 0))
			.build();

		//when
		LocalDateTime sentAt = LocalDateTime.of(2025, 1, 2, 0, 0);
		record.success(sentAt);

		//then
		assertThat(record.getStatus()).isEqualTo(PaymentEventRecordStatus.SENT);
		assertThat(record.getSentAt()).isEqualTo(sentAt);
	}

	@Test
	@DisplayName("PaymentSuccessEventRecord 의 failed 를 호출하는 경우, 상태를 FAILED 로 변경한다.")
	void shouldStatusFailedAndSentAtLocalDateTimeWhenCalledFailed() {
		//given
		PaymentSuccessEventRecord record = PaymentSuccessEventRecord.builder()
			.status(PaymentEventRecordStatus.SENDING)
			.sentAt(LocalDateTime.of(2025, 1, 1, 0, 0))
			.build();

		//when
		record.failed();

		//then
		assertThat(record.getStatus()).isEqualTo(PaymentEventRecordStatus.FAILED);
	}

	@Test
	@DisplayName("PaymentSuccessEventRecord 의 sending 을 호출하는 경우, 상태를 SENDING 으로 변경한다.")
	void shouldStatusSendingAndSentAtLocalDateTimeWhenCalledSending() {
		//given
		PaymentSuccessEventRecord record = PaymentSuccessEventRecord.builder()
			.status(PaymentEventRecordStatus.FAILED)
			.sentAt(LocalDateTime.of(2025, 1, 1, 0, 0))
			.build();

		//when
		record.sending();

		//then
		assertThat(record.getStatus()).isEqualTo(PaymentEventRecordStatus.SENDING);
	}

}