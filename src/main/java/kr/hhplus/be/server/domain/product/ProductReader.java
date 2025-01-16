package kr.hhplus.be.server.domain.product;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductReader {

	private final ProductRepository productRepository;

	public Product read(long productId) {
		return productRepository.findById(productId);
	}

}
