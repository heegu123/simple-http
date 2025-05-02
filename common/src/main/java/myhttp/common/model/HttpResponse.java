package myhttp.common.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpResponse implements HttpMessage{

    private final String version; // HTTP 버전
    private final HttpStatus status; // HTTP 상태 코드
    private final String reason; // 상태 메시지
    private final HttpHeaders headers;  // HTTP 헤더
    private final byte[] body; // HTTP 본문

    // 생성자, HTTP 버전, 상태 코드, 상태 메시지, 헤더, 본문을 매개변수로 받음, Null 체크
    public HttpResponse(String version, HttpStatus status, String reason, HttpHeaders headers, byte[] body) {
        this.version = Objects.requireNonNull(version, "Version cannot be null").trim();
        this.status = Objects.requireNonNull(status, "HttpStatus cannot be null");
        this.reason = Objects.requireNonNull(reason, "Reason cannot be null");
        this.headers = Objects.requireNonNull(headers, "HttpHeaders cannot be null");
        this.body = (body == null ? new byte[0] : body);
    }

    public String getVersion() { return version; }
    public HttpStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public HttpHeaders getHeaders() { return headers; }
    public byte[] getBody() { return body; }

    // HTTP 응답의 상태라인을 반환하는 메서드
    @Override
    public String startLine() {
        return version + " " + status.getCode() + " " + reason;

    }

   // 바이트배열 형태로 HTTP 응답을 반환하는 메서드
    @Override
    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream(); // ByteArrayOutputStream 객체 생성
            out.write((startLine() + "\r\n").getBytes(StandardCharsets.UTF_8)); // 상태라인을 바이트 배열로 변환하여 출력 스트림에 씀
            out.write(headers.toRawString().getBytes(StandardCharsets.UTF_8)); // 헤더를 바이트 배열로 변환하여 출력 스트림에 씀
            if (body.length > 0) {
                // 본문이 있는 경우
                out.write(body);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to build HTTP response bytes", e);
        }
    }

    @Override
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }
}
