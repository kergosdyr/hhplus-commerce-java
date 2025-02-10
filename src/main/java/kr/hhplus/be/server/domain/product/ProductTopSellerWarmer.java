package kr.hhplus.be.server.domain.product;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductTopSellerWarmer {

	public static final String TOP_SELLERS_CACHE = "topSellersCache";
	public static final int DEFAULT_DAYS = 3;
	public static final String TIME_23_AND_HALF = "84600000";
	private final ProductService productService;

	private final CacheManager cacheManager;

	@Scheduled(timeUnit = TimeUnit.MILLISECONDS, fixedDelayString = TIME_23_AND_HALF)
	public void warming() {

		var cache = cacheManager.getCache(TOP_SELLERS_CACHE);
		if (cache == null) {
			return;
		}
		cache.clear();
		cache.put(DEFAULT_DAYS, productService.findAllTopSellers(DEFAULT_DAYS));
	}

}
