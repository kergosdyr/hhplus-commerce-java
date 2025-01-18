package kr.hhplus.be.server.api.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain
	) throws ServletException, IOException {

		ContentCachingRequestWrapper cachingRequest = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper cachingResponse = new ContentCachingResponseWrapper(response);

		String requestId = UUID.randomUUID().toString();
		MDC.put("requestId", requestId);

		String method = request.getMethod();
		String uri = request.getRequestURI();
		String clientIp = getClientIpAddress(request);

		MDC.put("clientIp", clientIp);
		MDC.put("method", method);
		MDC.put("uri", uri);

		log.info("===== [Request Start] [{}] [{}] {} [IP: {}]", requestId, method, uri, clientIp);

		try {
			filterChain.doFilter(cachingRequest, cachingResponse);
		} finally {
			byte[] requestBody = cachingRequest.getContentAsByteArray();
			if (requestBody.length > 0) {
				String bodyString = new String(requestBody, StandardCharsets.UTF_8);
				log.info("[RequestId: {}] Request Body: {}", requestId, bodyString);
			}

			byte[] responseBody = cachingResponse.getContentAsByteArray();
			if (responseBody.length > 0) {
				String responseString = new String(responseBody, StandardCharsets.UTF_8);
				log.info("[RequestId: {}] Response Body: {}", requestId, responseString);
			}

			int status = cachingResponse.getStatus();
			log.info("[RequestId: {}] Response Status: {}", requestId, status);

			cachingResponse.copyBodyToResponse();

			MDC.remove("requestId");
			MDC.remove("clientIp");
			MDC.remove("method");
			MDC.remove("uri");
		}
	}

	private String getClientIpAddress(HttpServletRequest request) {
		String[] headerNames = {
			"X-Forwarded-For",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR"
		};

		for (String header : headerNames) {
			String ipList = request.getHeader(header);
			if (ipList != null && !ipList.isEmpty() && !"unknown".equalsIgnoreCase(ipList)) {
				return ipList.split(",")[0].trim();
			}
		}

		return request.getRemoteAddr();
	}
}
