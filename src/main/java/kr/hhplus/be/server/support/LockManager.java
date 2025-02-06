package kr.hhplus.be.server.support;

import java.util.List;

public interface LockManager {
	String acquire(String lockKey);

	List<String> acquireAll(List<String> lockKeys);

	void release(String lockKey);

	void releaseAll(List<String> lockKeys);
}
