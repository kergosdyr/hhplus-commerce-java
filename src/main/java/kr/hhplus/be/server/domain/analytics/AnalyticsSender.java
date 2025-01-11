package kr.hhplus.be.server.domain.analytics;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsSender {

	private final AnalyticsClient analyticsClient;

	public boolean send(AnalyticData analyticData) {
		return analyticsClient.send(analyticData);
	}

}
