package kr.hhplus.be.server.config;

import org.junit.jupiter.api.BeforeEach;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.restassured.RestAssured;
import kr.hhplus.be.server.domain.balanace.BalanceModifier;
import kr.hhplus.be.server.domain.balanace.BalanceReader;
import kr.hhplus.be.server.domain.balanace.BalanceService;
import kr.hhplus.be.server.domain.coupon.CouponIssuer;
import kr.hhplus.be.server.domain.coupon.UserCouponIssueScheduler;
import kr.hhplus.be.server.domain.coupon.UserCouponValidator;
import kr.hhplus.be.server.domain.order.OrderGenerator;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.product.ProductFinder;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.domain.product.ProductStockModifier;
import kr.hhplus.be.server.infra.storage.balance.BalanceJpaRepository;
import kr.hhplus.be.server.infra.storage.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infra.storage.coupon.UserCouponJpaRepository;
import kr.hhplus.be.server.infra.storage.coupon.UserCouponRedissonRepository;
import kr.hhplus.be.server.infra.storage.order.OrderDetailJpaRepository;
import kr.hhplus.be.server.infra.storage.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.storage.payment.PaymentJpaRepository;
import kr.hhplus.be.server.infra.storage.product.ProductJpaRepository;
import kr.hhplus.be.server.infra.storage.product.ProductStockJpaRepository;
import kr.hhplus.be.server.infra.storage.user.UserJpaRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected ProductStockJpaRepository productStockJpaRepository;

	@LocalServerPort
	int port;

	@Autowired
	protected BalanceJpaRepository balanceJpaRepository;

	@Autowired
	protected BalanceReader balanceReader;

	@Autowired
	protected OrderService orderService;

	@Autowired
	protected OrderGenerator orderGenerator;

	@Autowired
	protected OrderDetailJpaRepository orderDetailJpaRepository;

	@Autowired
	protected PaymentService paymentService;

	@Autowired
	protected BalanceModifier balanceModifier;

	@Autowired
	protected BalanceService balanceService;

	@Autowired
	protected UserJpaRepository userJpaRepository;

	@Autowired
	protected CouponJpaRepository couponJpaRepository;

	@Autowired
	protected ApplicationContext applicationContext;


	@Autowired
	protected UserCouponJpaRepository userCouponJpaRepository;

	@Autowired
	protected OrderJpaRepository orderJpaRepository;

	@Autowired
	protected PaymentJpaRepository paymentJpaRepository;

	@Autowired
	protected ProductJpaRepository productJpaRepository;

	@Autowired
	protected CouponIssuer couponIssuer;

	@Autowired
	protected ProductStockModifier productStockModifier;

	@Autowired
	protected ProductStockJpaRepository productStockRepository;

	@Autowired
	protected UserCouponIssueScheduler userCouponIssueScheduler;

	@Autowired
	protected RedissonClient redissonClient;

	@Autowired
	protected UserCouponRedissonRepository userCouponRedissonRepository;

	@Autowired
	protected UserCouponValidator userCouponValidator;

	@Autowired
	protected ProductService productService;

	@MockitoSpyBean
	protected ProductFinder productFinder;

	@Autowired
	protected CacheManager redissonCacheManager;

	@BeforeEach
	void setUp() {
		RestAssured.port = port;
	}

	@BeforeEach
	void init() {
		couponJpaRepository.deleteAllInBatch();
		orderJpaRepository.deleteAllInBatch();
		balanceJpaRepository.deleteAllInBatch();
		paymentJpaRepository.deleteAllInBatch();
		productJpaRepository.deleteAllInBatch();
		orderDetailJpaRepository.deleteAllInBatch();
		userCouponJpaRepository.deleteAllInBatch();
		userJpaRepository.deleteAllInBatch();
		productStockJpaRepository.deleteAllInBatch();
		redissonClient.getKeys().flushall();
	}

}
