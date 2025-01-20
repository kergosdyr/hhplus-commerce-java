package kr.hhplus.be.server.config;

import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import kr.hhplus.be.server.error.ApiException;
import kr.hhplus.be.server.error.ErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@ConditionalOnBean(LockManager.class)
@Slf4j
public class WithLockAspect {

	private final LockManager lockManager;

	@Around("@annotation(withLock)")
	public Object applyLock(ProceedingJoinPoint joinPoint, WithLock withLock) throws Throwable {
		String key = resolveKey(joinPoint, withLock.key());
		String lockValue = lockManager.acquire(key);

		if (lockValue == null) {
			throw new ApiException(ErrorType.DEFAULT_ERROR);
		}

		boolean isTransactionActive = TransactionSynchronizationManager.isSynchronizationActive();

		try {
			if (isTransactionActive) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCompletion(int status) {
						lockManager.release(key);
					}
				});
			}
			return joinPoint.proceed();
		} catch (Exception e) {
			throw e;
		} finally {
			if (!isTransactionActive) {
				lockManager.release(key);
			}
		}
	}

	private String resolveKey(ProceedingJoinPoint joinPoint, String spel) {
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		Object[] args = joinPoint.getArgs();
		String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();

		IntStream.range(0, args.length).forEach(i -> context.setVariable(parameterNames[i], args[i]));

		return parser.parseExpression(spel).getValue(context, String.class);
	}
}
