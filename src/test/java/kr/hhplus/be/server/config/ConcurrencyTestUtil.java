package kr.hhplus.be.server.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public final class ConcurrencyTestUtil {

	private ConcurrencyTestUtil() {
	}

	public static <T> ConcurrencyTestResult<T> run(int threadPoolSize, Supplier<T> taskSupplier)
		throws InterruptedException {
		ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
		CountDownLatch latch = new CountDownLatch(1);
		List<Future<T>> futures = new ArrayList<>();

		for (int i = 0; i < threadPoolSize; i++) {
			futures.add(executorService.submit(() -> {
				latch.await();
				return taskSupplier.get();
			}));
		}

		latch.countDown();
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);

		return new ConcurrencyTestResult<>(futures);
	}

	public static class ConcurrencyTestResult<T> {
		private final List<Future<T>> futures;

		public ConcurrencyTestResult(List<Future<T>> futures) {
			this.futures = futures;
		}

		public long success() {
			return futures.stream()
				.filter(f -> {
					try {
						return (boolean)f.get();
					} catch (Exception e) {
						return false;
					}
				})
				.count();
		}

		public long fail() {
			return futures.stream()
				.filter(f -> {
					try {
						return !(boolean)f.get();
					} catch (Exception e) {
						return false;
					}
				})
				.count();
		}
	}
}