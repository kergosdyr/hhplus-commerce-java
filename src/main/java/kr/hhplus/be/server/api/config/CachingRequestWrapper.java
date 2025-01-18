package kr.hhplus.be.server.api.config;

import java.io.IOException;

import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;

public class CachingRequestWrapper extends ContentCachingRequestWrapper {
	public CachingRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
	}
}
