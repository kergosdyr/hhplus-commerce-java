package kr.hhplus.be.server.domain.analytics;

public interface AnalyticsClient {
	boolean send(AnalyticData analyticData);
}
