package kr.hhplus.be.server.domain.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

	@Mock
	AnalyticsSender analyticsSender;

	@InjectMocks
	AnalyticsService analyticsService;

	@Test
	@DisplayName("analyticsService 의 send 를 호출하는 경우 true 를 리턴한다")
	void shouldWhenAnalyticsServiceSendCalledThenReturnTrue() {
		//given
		when(analyticsSender.send(any(AnalyticData.class))).thenReturn(true);

		//when
		boolean result = analyticsService.analyze(new AnalyticData(1L, 2L, LocalDateTime.of(2025, 1, 1, 0, 0, 0)));

		//then
		assertThat(result).isTrue();
	}

}