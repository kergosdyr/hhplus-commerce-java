package kr.hhplus.be.server.domain.payment;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.hhplus.be.server.config.IntegrationTest;
import kr.hhplus.be.server.enums.PaymentEventRecordStatus;

class PaymentSuccessEventSchedulerIntegrationTest extends IntegrationTest {

	@Test
	@DisplayName("outbox 에 failed 로 남아있는 레코드가 존재할 때 schedule 이 호출되면 메세지가 재발송된다")
	void shouldResendingMessageWhenOutboxHasFailedRecord() {
		//given
		PaymentSuccessEventRecord savedRecord = paymentSuccessEventRecordJpaRepository.save(
			PaymentSuccessEventRecord.builder()
				.orderId(111)
				.paymentId(222)
				.orderCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0))
				.status(PaymentEventRecordStatus.FAILED)
				.build());

		//when
		paymentSuccessEventScheduler.paymentSuccessEventReSendSchedule();

		//then
		await()
			.pollInterval(Duration.ofMillis(500))
			.atMost(Duration.ofSeconds(10))
			.untilAsserted(() -> verify(analyticsListener, atLeastOnce()).whenPaymentSuccess(any()));

	}

}