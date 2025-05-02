package myhttp.common.builder;

import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpMethod;
import myhttp.common.model.HttpRequest;

import java.nio.charset.StandardCharsets;

public class HttpRequestBuilder {

    private HttpMethod method; // HTTP 메소드
    private String path; // 요청 경로
    private String version = "HTTP/1.1"; // HTTP 버전
    private final HttpHeaders headers = new HttpHeaders(); // 요청 헤더
    private byte[] body = new byte[0]; // 요청 본문

    // 메소드 체이닝을 위한 빌더 메소드들
    public HttpRequestBuilder withMethod(HttpMethod m) {
        this.method = m;
        return this;
    }
    public HttpRequestBuilder withPath(String p) {
        this.path = p;
        return this;
    }
    public HttpRequestBuilder addHeader(String name, String value) {
        headers.addHeader(name, value); // 헤더 추가
        return this;
    }
    public HttpRequestBuilder withBody(String b) {
        this.body = b.getBytes(StandardCharsets.UTF_8);
        headers.setHeader("Content-Length", String.valueOf(body.length));
        return this;
    }

    public HttpRequestBuilder withBody(byte[] b) {
        this.body = b;
        headers.setHeader("Content-Length", String.valueOf(b.length));
        return this;
    }

    // 빌더 메소드
    public HttpRequest build() {
        if (method == null || path == null) { // 메소드와 경로가 필수
            throw new IllegalStateException("Method and path required");
        }
        return new HttpRequest(method, path, version, headers, body);
    }
}
