package kr.hhplus.be.server.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;

import kr.hhplus.be.server.config.IntegrationTest;

public class KafkaRunningTest extends IntegrationTest {

	public static final String TOPIC_ID = "topic1";
	public static final String TEST_SEND_MESSAGE = "testSendMessage";

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;


	@Autowired
	private ConsumerFactory<String, String> consumerFactory;

	@Test
	@DisplayName("Kafka 가 테스트컨테이너 환경에서 topic1 에 testSendMessage 를 주고받을 수 있는지 검증한다.")
	void shouldKafkaRunningNormallyInTestContainer() throws ExecutionException, InterruptedException {

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
		SendResult<String, String> sendResult = kafkaTemplate.send(TOPIC_ID, TEST_SEND_MESSAGE).get();
		kafkaTemplate.flush();
		Assertions.assertThat(sendResult.getRecordMetadata().hasOffset()).isTrue();


		//then
		await()
			.pollInterval(Duration.ofMillis(500))
			.atMost(Duration.ofSeconds(10))
			.untilAsserted(() -> assertThat(assertionValue.get()).isEqualTo(TEST_SEND_MESSAGE));

	}

}
