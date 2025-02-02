package kr.hhplus.be.server.support;

public interface LockManager {
	String acquire(String lockKey);

	void release(String lockKey);
}
