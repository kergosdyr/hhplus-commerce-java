package kr.hhplus.be.server.domain.analytics;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	private final AnalyticsSender analyticsSender;

	public boolean analyze(AnalyticData analyticData) {
		return analyticsSender.send(analyticData);
	}

}
