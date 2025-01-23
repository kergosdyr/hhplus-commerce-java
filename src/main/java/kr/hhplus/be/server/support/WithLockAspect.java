package kr.hhplus.be.server.support;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.Order;
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
@Order(0)
@Slf4j
public class WithLockAspect {

	private final LockManager lockManager;

	@Around("@annotation(withLock) && execution(* *(..))")
	public Object applyLock(ProceedingJoinPoint joinPoint, WithLock withLock) throws Throwable {
		String singleKey = resolveKey(joinPoint, withLock.key());
		List<String> multipleKeys = resolveKeys(joinPoint, withLock.keys());

		List<String> allKeys = (multipleKeys != null && !multipleKeys.isEmpty())
			? multipleKeys
			: (isNotBlank(singleKey) ? List.of(singleKey) : List.of());

		if (allKeys.isEmpty()) {
			throw new ApiException(ErrorType.DEFAULT_ERROR);
		}

		allKeys.forEach(key -> {
			if (isBlank(lockManager.acquire(key))) {
				throw new ApiException(ErrorType.DEFAULT_ERROR);
			}
		});

		try {
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCompletion(int status) {
						releaseAllLocks(allKeys);
					}
				});
			}

			return joinPoint.proceed();
		} finally {
			if (!TransactionSynchronizationManager.isSynchronizationActive()) {
				releaseAllLocks(allKeys);
			}
		}
	}

	private String resolveKey(ProceedingJoinPoint joinPoint, String spel) {
		if (isBlank(spel)) {
			return "";
		}
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		populateContext(joinPoint, context);

		return parser.parseExpression(spel).getValue(context, String.class);
	}

	private List<String> resolveKeys(ProceedingJoinPoint joinPoint, String spel) {
		if (isBlank(spel)) {
			return List.of();
		}
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		populateContext(joinPoint, context);

		Object result = parser.parseExpression(spel).getValue(context);
		if (result instanceof List) {
			return ((List<?>)result).stream().map(Object::toString).toList();
		} else if (result instanceof String[]) {
			return Arrays.asList((String[])result);
		} else if (result instanceof String) {
			return List.of((String)result);
		}
		throw new ApiException(ErrorType.DEFAULT_ERROR);
	}

	private void populateContext(ProceedingJoinPoint joinPoint, StandardEvaluationContext context) {
		Object[] args = joinPoint.getArgs();
		String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		IntStream.range(0, args.length).forEach(i -> context.setVariable(parameterNames[i], args[i]));
	}

	private void releaseAllLocks(List<String> keys) {
		IntStream.range(0, keys.size())
			.forEach(i -> lockManager.release(keys.get(i)));
	}

}
