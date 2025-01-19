package kr.hhplus.be.server.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.hhplus.be.server.domain.balanace.BalanceService;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.ProductService;

@org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
@ExtendWith(MockitoExtension.class)
public class WebMvcTest {

	@Autowired
	protected ObjectMapper objectMapper;


	@Autowired
	protected MockMvc mockMvc;

	@MockitoBean
	protected BalanceService balanceService;

	@MockitoBean
	protected CouponService couponService;

	@MockitoBean
	protected OrderService orderService;

	@MockitoBean
	protected ProductService productService;




}
