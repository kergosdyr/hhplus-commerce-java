package kr.hhplus.be.server.infra.clients.analytics;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.analytics.AnalyticData;
import kr.hhplus.be.server.domain.analytics.AnalyticsClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AnalyticsClientImpl implements AnalyticsClient {
	@Override
	public boolean send(AnalyticData analyticData) {
		log.info("analytics Data sending ......");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		log.info("analytics Data sending done ......");
		return true;
	}
}
