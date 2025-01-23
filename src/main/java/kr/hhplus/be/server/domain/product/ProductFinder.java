package kr.hhplus.be.server.domain.product;

import static io.micrometer.common.util.StringUtils.isBlank;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductFinder {

	private final ProductRepository productRepository;

	public List<Product> find(String keyword, int page, int size) {

		if (isBlank(keyword)) {
			return productRepository.findAll(page, size);
		}

		return productRepository.findAllByKeyword(keyword, page, size);

	}

	public long findCount(String keyword) {

		if (isBlank(keyword)) {
			return productRepository.countAll();
		}

		return productRepository.countAllByKeyword(keyword);
	}

	public List<ProductSellerOutput> findAllTopSellers(int days) {
		return productRepository.findAllTopSellers(days);
	}
}
