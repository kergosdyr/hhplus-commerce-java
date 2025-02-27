package kr.hhplus.be.server;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PreDestroy;

@Configuration
@Testcontainers
class TestcontainersConfiguration {

	public static final MySQLContainer<?> MYSQL_CONTAINER;
	public static final GenericContainer<?> REDIS_CONTAINER;
	public static final GenericContainer<?> KAFKA_CONTAINER;

	public static final Network network = Network.newNetwork();


	static {
		MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
			.withDatabaseName("hhplus")
			.withUsername("test")
			.withPassword("test");
		MYSQL_CONTAINER.start();

		System.setProperty("spring.datasource.url", MYSQL_CONTAINER.getJdbcUrl() + "?characterEncoding=UTF-8&serverTimezone=UTC");
		System.setProperty("spring.datasource.username", MYSQL_CONTAINER.getUsername());
		System.setProperty("spring.datasource.password", MYSQL_CONTAINER.getPassword());

		// Redis 컨테이너 설정
		REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse("redis:7.4.2"))
			.withExposedPorts(6379);

		REDIS_CONTAINER.setPortBindings(List.of("6379:6379"));

		REDIS_CONTAINER.start();

		// Kafka 컨테이너 설정 (Bitnami Kafka)
		KAFKA_CONTAINER = new GenericContainer<>(DockerImageName.parse("bitnami/kafka:latest"))
			.withExposedPorts(9094, 9092, 9093)
			.withNetwork(network)
			.withNetworkAliases("kafka")
			.withEnv("KAFKA_CFG_NODE_ID", "0")
			.withEnv("KAFKA_CFG_PROCESS_ROLES", "controller,broker")
			.withEnv("KAFKA_CFG_LISTENERS", "PLAINTEXT://:9092,CONTROLLER://:9093,EXTERNAL://:9094")
			.withEnv("KAFKA_CFG_ADVERTISED_LISTENERS", "PLAINTEXT://kafka:9092,EXTERNAL://localhost:9094")
			.withEnv("KAFKA_CFG_LISTENER_SECURITY_PROTOCOL_MAP",
				"CONTROLLER:PLAINTEXT,EXTERNAL:PLAINTEXT,PLAINTEXT:PLAINTEXT")
			.withEnv("KAFKA_CFG_CONTROLLER_QUORUM_VOTERS", "0@kafka:9093")
			.withEnv("KAFKA_CFG_CONTROLLER_LISTENER_NAMES", "CONTROLLER")
			.waitingFor(Wait.forLogMessage(".*Kafka Server started.*\\n", 1));

		KAFKA_CONTAINER.setPortBindings(List.of("9094:9094"));

		KAFKA_CONTAINER.start();

		createTopic(KAFKA_CONTAINER, "topic1", 1, 1);
		createTopic(KAFKA_CONTAINER, "paymentSuccess", 1, 1);

	}

	private static void createTopic(GenericContainer<?> container, String topic, int partitions,
		int replicationFactor) {
		// kafka-topics.sh 스크립트를 통해 토픽 생성
		Container.ExecResult result = null;
		try {
			result = container.execInContainer(
				"/opt/bitnami/kafka/bin/kafka-topics.sh",
				"--create",
				"--if-not-exists",
				"--topic", topic,
				"--bootstrap-server", "localhost:9092",
				"--partitions", String.valueOf(partitions),
				"--replication-factor", String.valueOf(replicationFactor)
			);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (result.getExitCode() != 0) {
			throw new RuntimeException("토픽 생성 실패 (" + topic + "): " + result.getStderr());
		} else {
			System.out.println("토픽 생성 성공: " + topic);
		}
	}


	@DynamicPropertySource
	static void registerRedisProperties(DynamicPropertyRegistry registry) {
		String redisHost = REDIS_CONTAINER.getHost();
		Integer redisPort = REDIS_CONTAINER.getFirstMappedPort();

		registry.add("spring.data.redis.host", () -> redisHost);
		registry.add("spring.data.redis.port", () -> redisPort);

		// Kafka bootstrap 서버 프로퍼티 등록
		// 컨테이너가 매핑한 외부 포트(9094)를 사용합니다.
		String kafkaBootstrap = KAFKA_CONTAINER.getHost() + ":" + KAFKA_CONTAINER.getMappedPort(9094);
		registry.add("spring.kafka.bootstrap-servers", () -> kafkaBootstrap);

	}


	@PreDestroy
	public void preDestroy() {
		if (MYSQL_CONTAINER.isRunning()) {
			MYSQL_CONTAINER.stop();
		}
		if (REDIS_CONTAINER.isRunning()) {
			REDIS_CONTAINER.stop();
		}
		if (KAFKA_CONTAINER.isRunning()) {
			KAFKA_CONTAINER.stop();
		}

	}
}