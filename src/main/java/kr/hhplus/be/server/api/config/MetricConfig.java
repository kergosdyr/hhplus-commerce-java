package kr.hhplus.be.server.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricConfig {
	@Bean
	MeterRegistryCustomizer<MeterRegistry> configurer(
		@Value("${spring.application.name}") String applicationName) {
		return (registry) -> registry.config().commonTags("application", applicationName);
	}

}
