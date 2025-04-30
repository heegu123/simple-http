package myhttp.common.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class HttpResponse implements HttpMessage{

    private final String version;
    private final HttpStatus status;
    private final String reason;
    private final HttpHeaders headers;
    private final byte[] body;

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

    @Override
    public String startLine() {
        return version + " " + status.getCode() + " " + reason;

    }

    @Override
    public byte[] toByteArray() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write((startLine() + "\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(headers.toRawString().getBytes(StandardCharsets.UTF_8));
            if (body.length > 0) {
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
