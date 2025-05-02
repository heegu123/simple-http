package myhttp.common.builder;

import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpResponse;
import myhttp.common.model.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpResponseBuilder {

    private String version = "HTTP/1.1"; // HTTP 버전
    private HttpStatus status; // 상태 코드
    private String reason; // 상태 메시지
    private final HttpHeaders headers = new HttpHeaders(); // 헤더
    private byte[] body = new byte[0]; // 본문

    // 요청 헤더
    private HttpHeaders requestHeaders;
    public HttpResponseBuilder withRequestHeaders(HttpHeaders reqHeaders) {
        this.requestHeaders = reqHeaders;
        return this;
    }

    // 빌더 메서드
    public HttpResponseBuilder withVersion(String v) {
        this.version = v; return this;
    }
    public HttpResponseBuilder withStatus(HttpStatus s) {
        this.status = s;
        this.reason = s.getReason();
        return this;
    }
    public HttpResponseBuilder withReasonPhrase(String r) {
        this.reason = r;
        return this;
    }
    public HttpResponseBuilder addHeader(String name, String value) {
        headers.addHeader(name, value);
        return this;
    }
    public HttpResponseBuilder withBody(String b) {
        this.body = b.getBytes(StandardCharsets.UTF_8);
        headers.setHeader("Content-Length", String.valueOf(body.length));
        return this;
    }
    public HttpResponseBuilder withBody(byte[] b) {
        this.body = b;
        headers.setHeader("Content-Length", String.valueOf(b.length));
        return this;
    }

    // 빌드 메서드
    public HttpResponse build() {
        if (status == null) throw new IllegalStateException("Status required"); // 상태 코드가 없으면 예외 발생
        // Date 헤더 추가
        headers.setHeader("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
        // Server 헤더 추가, 서버 이름은 MyHttpServer로 설정
        headers.setHeader("Server", "MyHttpServer/1.0");
        // 지속 연결 헤더설정
        String connection = requestHeaders != null // 요청 헤더가 있는 경우
                ? requestHeaders.getFirst("Connection").orElse("").toLowerCase() // Connection 헤더 값
                : "";
        if ("close".equals(connection)) { // 지속 연결을 하지 않음
            headers.setHeader("Connection", "close"); // 지속 연결을 하지 않음
        } else {
            headers.setHeader("Connection", "keep-alive"); // 지속 연결
        }
        headers.setHeader("Content-Language", "ko-KR"); // 언어 설정
        headers.setHeader("Cache-Control", "no-store"); // 캐시 설정
        return new HttpResponse(version, status, reason, headers, body);
    }
}
