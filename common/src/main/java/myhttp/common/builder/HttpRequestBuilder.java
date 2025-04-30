package myhttp.common.builder;

import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpMethod;
import myhttp.common.model.HttpRequest;

import java.nio.charset.StandardCharsets;

public class HttpRequestBuilder {

    private HttpMethod method;
    private String path;
    private String version = "HTTP/1.1";
    private final HttpHeaders headers = new HttpHeaders();
    private byte[] body = new byte[0];

    public HttpRequestBuilder withMethod(HttpMethod m) {
        this.method = m;
        return this;
    }
    public HttpRequestBuilder withPath(String p) {
        this.path = p;
        return this;
    }
    public HttpRequestBuilder addHeader(String name, String value) {
        headers.addHeader(name, value);
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
    public HttpRequest build() {
        if (method == null || path == null) {
            throw new IllegalStateException("Method and path required");
        }
        return new HttpRequest(method, path, version, headers, body);
    }
}
