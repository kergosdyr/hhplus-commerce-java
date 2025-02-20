package kr.hhplus.be.server.domain.analytics;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import kr.hhplus.be.server.domain.payment.PaymentSuccessEventRecodeModifier;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	private final AnalyticsSender analyticsSender;

	private final PaymentSuccessEventRecodeModifier paymentSuccessEventRecodeModifier;

	public boolean analyze(AnalyticData analyticData, LocalDateTime sentAt) {
		boolean isSent = analyticsSender.send(analyticData);
		paymentSuccessEventRecodeModifier.success(analyticData.paymentId(), sentAt);
		return isSent;
	}

}
