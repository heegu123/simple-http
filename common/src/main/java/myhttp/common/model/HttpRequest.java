package myhttp.common.model;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/*
* HTTP 요청 메시지 구조
* */
public class HttpRequest {

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpRequest(HttpMethod method, String path, String version, HttpHeaders headers, byte[] body) {
        this.method = Objects.requireNonNull(method, "HttpMethod cannot be null");
        this.path = Objects.requireNonNull(path, "Path cannot be null");
        this.version = Objects.requireNonNull(version, "Version cannot be null");
        this.headers = Objects.requireNonNull(headers, "HTTP Headers cannot be null");
        this.body = (body == null) ? new byte[0] : body;
    }

    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public String getVersion() { return version; }
    public HttpHeaders getHeaders() { return headers; }
    public byte[] getBody() { return body; }

    /*
    * 시작라인 "METHOD /PATH HTTP/1.1"
    * */
    public String startLine() {
        return method + " " + path + " " + version;
    }

    /*
    * 전체 메시지 구성( byte 배열 )
    * HTTP는 TCP 위에서 동작하고, TCP는 바이트 단위로 데이터 전송하기 때문에 ByteArray로 변환 -> Socket.getOutputStream().write()
     * */
    public byte[] toByteArray() {

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // 1. start line + CRLF
            out.write((startLine() + "\r\n").getBytes(StandardCharsets.UTF_8));
            // 2. headers
            out.write(headers.toRawString().getBytes(StandardCharsets.UTF_8));
            // 3. bpdy 이전 CRLF: headers.toRawString()에 CRLF 포함
            // 4. body
            if (body.length > 0) {
                out.write(body);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build HTTP Request bytes", e);
        }
    }
}

