package kr.hhplus.be.server.support;

import static org.apache.commons.lang3.StringUtils.isBlank;

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

	@Around("@annotation(withLock)")
	public Object applyLock(ProceedingJoinPoint joinPoint, WithLock withLock) throws Throwable {
		List<String> keys = resolveKeys(joinPoint, withLock.key());

		lockManager.acquireAll(keys);

		try {
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
					@Override
					public void afterCompletion(int status) {
						lockManager.releaseAll(keys);
					}
				});
			}

			return joinPoint.proceed();
		} finally {
			if (!TransactionSynchronizationManager.isSynchronizationActive()) {
				lockManager.releaseAll(keys);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> resolveKeys(ProceedingJoinPoint joinPoint, String spel) {
		if (isBlank(spel)) {
			throw new ApiException(ErrorType.DEFAULT_ERROR);
		}
		ExpressionParser parser = new SpelExpressionParser();
		StandardEvaluationContext context = new StandardEvaluationContext();
		populateContext(joinPoint, context);

		return parser.parseExpression(spel).getValue(context, List.class);
	}

	private void populateContext(ProceedingJoinPoint joinPoint, StandardEvaluationContext context) {
		Object[] args = joinPoint.getArgs();
		String[] parameterNames = ((MethodSignature)joinPoint.getSignature()).getParameterNames();
		IntStream.range(0, args.length).forEach(i -> context.setVariable(parameterNames[i], args[i]));
	}

}
