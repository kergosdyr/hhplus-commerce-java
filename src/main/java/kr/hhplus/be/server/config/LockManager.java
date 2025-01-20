package kr.hhplus.be.server.config;

public interface LockManager {
	String acquire(String lockKey);

	void release(String lockKey);
}
