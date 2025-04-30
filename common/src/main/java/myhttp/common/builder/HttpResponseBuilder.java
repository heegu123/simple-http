package myhttp.common.builder;

import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpResponse;
import myhttp.common.model.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class HttpResponseBuilder {

    private String version = "HTTP/1.1";
    private HttpStatus status;
    private String reason;
    private final HttpHeaders headers = new HttpHeaders();
    private byte[] body = new byte[0];

    //TODO : CHECK
    private HttpHeaders requestHeaders;
    public HttpResponseBuilder withRequestHeaders(HttpHeaders reqHeaders) {
        this.requestHeaders = reqHeaders;
        return this;
    }

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
    public HttpResponse build() {
        if (status == null) throw new IllegalStateException("Status required");
        headers.setHeader("Date", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
        headers.setHeader("Server", "MyHttpServer/1.0");
//        headers.setHeader("Connection", "keep-alive");
        String connection = requestHeaders != null
                ? requestHeaders.getFirst("Connection").orElse("").toLowerCase()
                : "";
        if ("close".equals(connection)) {
            headers.setHeader("Connection", "close");
        } else {
            headers.setHeader("Connection", "keep-alive");
        }
        headers.setHeader("Content-Language", "ko-KR");
        headers.setHeader("Cache-Control", "no-store");
        return new HttpResponse(version, status, reason, headers, body);
    }
}
