package kr.hhplus.be.server.infra.storage.config;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheManagerConfig {

	@Bean
	public CacheManager redissonCacheManager(RedissonClient redissonClient) {
		var config = new HashMap<String, CacheConfig>();
		config.put("topSellersCache", new CacheConfig(TimeUnit.HOURS.toMillis(24), TimeUnit.HOURS.toMillis(24)));

		return new RedissonSpringCacheManager(redissonClient);
	}

}
