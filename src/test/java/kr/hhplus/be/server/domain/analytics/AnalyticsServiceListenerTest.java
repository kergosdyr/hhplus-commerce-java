package kr.hhplus.be.server.domain.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEvent;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceListenerTest {

	@Mock
	private AnalyticsSender analyticsSender;

	@InjectMocks
	private AnalyticsServiceListener analyticsServiceListener;

	@Test
	@DisplayName("AnalyticsServiceListener의 success 를 호출하면 send 를 호출한다")
	void shouldAnalyticsServiceListenerSuccessThenCallSend() {
		//given
		PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent(
			1L, 1L, LocalDateTime.of(2025, 1, 1, 0, 0, 0, 0)
		);

		when(analyticsSender.send(any()))
			.thenReturn(true);

		//when
		analyticsServiceListener.whenPaymentSuccess(paymentSuccessEvent);

		//then
		verify(analyticsSender, times(1)).send(
			new AnalyticData(paymentSuccessEvent.paymentId(), paymentSuccessEvent.orderId(),
				paymentSuccessEvent.orderCreatedAt()));
	}

}