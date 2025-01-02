package kr.hhplus.be.server.util;

public enum MockJwt {

	MOCK(
		"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0LXVzZXIiLCJjbGFpbXMiOnsicm9sZSI6IlVTRVIifX0.dummy-signature");

	private final String token;

	MockJwt(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
