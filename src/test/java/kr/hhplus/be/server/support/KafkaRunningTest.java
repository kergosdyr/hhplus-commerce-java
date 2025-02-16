package kr.hhplus.be.server.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(
	properties = {
		"spring.kafka.consumer.auto-offset-reset=earliest",
	}
)
public class KafkaRunningTest {

	public static final String TOPIC_ID = "topic1";
	public static final String TEST_SEND_MESSAGE = "testSendMessage";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private ConsumerFactory<String, String> consumerFactory;

	@Test
	@DisplayName("Kafka 가 테스트컨테이너 환경에서 topic1 에 testSendMessage 를 주고받을 수 있는지 검증한다.")
	void shouldKafkaRunningNormallyInTestContainer() {

		//given
		AtomicReference<String> assertionValue = new AtomicReference<>("");

		ContainerProperties containerProperties = new ContainerProperties(TOPIC_ID);
		containerProperties.setGroupId("testGroup1");
		containerProperties.setMessageListener(
			(MessageListener<String, String>)data -> {
				assertionValue.set(data.value());
			}
		);

		KafkaMessageListenerContainer<String, String> container =
			new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
		container.start();

		//when
		kafkaTemplate.send(TOPIC_ID, TEST_SEND_MESSAGE);

		//then
		await()
			.pollInterval(Duration.ofSeconds(1))
			.atMost(Duration.ofSeconds(10))
			.untilAsserted(() -> assertThat(assertionValue.get()).isEqualTo(TEST_SEND_MESSAGE));

	}

}
