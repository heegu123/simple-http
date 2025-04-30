package myhttp.common.parser;

import myhttp.common.exception.HttpParseException;
import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpMethod;
import myhttp.common.model.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpRequestParser implements HttpMessageParser<HttpRequest> {

    @Override
    public HttpRequest parse(InputStream in) throws HttpParseException {
        try {
            // 1. 헤더를 \r\n\r\n 나올 때까지 읽어들임
            ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
            int prev = 0;
            int curr;
            while ((curr = in.read()) != -1) {
                headerBuf.write(curr);
                if (prev == '\r' && curr == '\n') {
                    byte[] b = headerBuf.toByteArray();
                    int len = b.length;
                    if (len >= 4
                            && b[len - 4] == '\r'
                            && b[len - 3] == '\n'
                            && b[len - 2] == '\r'
                            && b[len - 1] == '\n') {
                        break;
                    }
                }
                prev = curr;
            }
            String headerStr = headerBuf.toString(StandardCharsets.UTF_8);
            String[] lines = headerStr.split("\r\n");

            // 2. start line 파싱
            String[] start = lines[0].split(" ", 3);
            if (start.length != 3) {
                throw new HttpParseException("Invalid request line: " + lines[0]);
            }
            HttpMethod method = HttpMethod.fromString(start[0]);
            String path = start[1];
            String version = start[2];

            // 3. 헤더 파싱
            HttpHeaders headers = new HttpHeaders();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    continue;
                }
                String[] kv = line.split(":", 2); //key-value, kv[0]: key, kv[1]: value
                if (kv.length != 2) {
                    throw new HttpParseException("Invalid header: " + line);
                }
                headers.addHeader(kv[0], kv[1]);
            }

            // 4. 바디 읽기
            int length = headers.getFirst("Content-Length")
                    .map(Integer::parseInt)
                    .orElse(0);
            byte[] body = new byte[length];
            int read = 0;
            while (read < length) {
                int r = in.read(body, read, length - read);
                if (r == -1) {
                    break;
                }
                read += r;
            }
            if (read != length) {
                throw new HttpParseException("Expected: " + length + "bytes, got " + read);
            }

            return new HttpRequest(method, path, version, headers, body);

        } catch (IOException e) {
            throw new HttpParseException("Error parsing HTTP Request", e);
        }
    }
}