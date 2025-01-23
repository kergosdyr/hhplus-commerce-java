package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductFinder productFinder;

	public List<Product> find(String keyword, int size, int page) {
		return productFinder.find(keyword, size, page);
	}

	public long findCount(String keyword) {
		return productFinder.findCount(keyword);
	}

	public List<ProductSellerOutput> findAllTopSellers(int days) {
		return productFinder.findAllTopSellers(days);

	}




}
