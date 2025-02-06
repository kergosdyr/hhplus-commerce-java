package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductFinder productFinder;

	public List<Product> find(String keyword, int page, int size) {
		return productFinder.find(keyword, page, size);
	}

	public long findCount(String keyword) {
		return productFinder.findCount(keyword);
	}

	@Cacheable(value = "topSellersCache", key = "#days", cacheManager = "redissonCacheManager")
	public List<ProductSellerOutput> findAllTopSellers(int days) {
		return productFinder.findAllTopSellers(days);
	}




}
