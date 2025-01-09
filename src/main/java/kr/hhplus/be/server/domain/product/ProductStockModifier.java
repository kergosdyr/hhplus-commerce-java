package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.domain.order.OrderProduct;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductStockModifier {

	private final ProductStockRepository productStockRepository;

	@Transactional
	public List<ProductStock> sell(List<OrderProduct> orderProducts) {

		return orderProducts.stream().map(orderProduct -> {

			ProductStock productStock = productStockRepository.findByProductId(orderProduct.productId())
				.orElseThrow(() -> new ApiException(ErrorType.PRODUCT_NOT_FOUND));

			if (!productStock.isSellable(orderProduct.quantity())) {
				throw new ApiException(ErrorType.PRODUCT_NO_STOCK);
			}

			productStock.sell(orderProduct.quantity());
			return productStock;

		}).toList();

	}
}
