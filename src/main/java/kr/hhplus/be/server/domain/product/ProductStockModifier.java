package kr.hhplus.be.server.domain.product;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.hhplus.be.server.config.WithLock;
import kr.hhplus.be.server.domain.order.OrderCommand;
import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductStockModifier {

	private final ProductStockRepository productStockRepository;

	@Transactional
	@WithLock(keys = "#orderCommands.![ 'product:' + productId ]")
	public List<ProductStock> sell(List<OrderCommand> orderCommands) {

		return orderCommands.stream().map(orderProduct -> {

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
