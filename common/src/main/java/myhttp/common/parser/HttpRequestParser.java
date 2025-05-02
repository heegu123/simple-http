package myhttp.common.parser;

import myhttp.common.exception.HttpParseException;
import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpMethod;
import myhttp.common.model.HttpRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/*
    * HttpRequestParser는 HTTP 요청 메시지를 파싱하는 클래스
    * 이 클래스는 InputStream을 읽어들여 HTTP 요청 메시지를 파싱하고,
    * HttpRequest 객체를 생성하여 반환한다.
* */
public class HttpRequestParser implements HttpMessageParser<HttpRequest> {

    @Override
    public HttpRequest parse(InputStream in) throws HttpParseException {
        try {
            // 1. 헤더를 \r\n\r\n 나올 때까지 읽어들임
            ByteArrayOutputStream headerBuf = new ByteArrayOutputStream();
            int prev = 0; // 이전 바이트를 저장하기 위한 변수
            int curr; // 현재 바이트를 저장하기 위한 변수
            while ((curr = in.read()) != -1) { // -1: EOF
                headerBuf.write(curr); // 읽어들인 바이트를 버퍼에 저장
                if (prev == '\r' && curr == '\n') { // CRLF
                    byte[] b = headerBuf.toByteArray(); // 바이트 배열로 변환
                    int len = b.length; // 버퍼의 길이
                    if (len >= 4 // 헤더의 길이가 4 이상이어야 함
                            && b[len - 4] == '\r' // CR
                            && b[len - 3] == '\n' // LF
                            && b[len - 2] == '\r' // CR
                            && b[len - 1] == '\n') { // LF
                        break; // 헤더가 끝났으므로 반복문 종료
                    }
                }
                prev = curr; // 현재 바이트를 이전 바이트로 저장
            }
            String headerStr = headerBuf.toString(StandardCharsets.UTF_8); // 바이트 배열을 문자열로 변환
            String[] lines = headerStr.split("\r\n"); // 헤더를 줄 단위로 나눔

            // 2. start line 파싱
            String[] start = lines[0].split(" ", 3); // start line은 3개로 나뉨
            if (start.length != 3) { // start line이 3개로 나뉘지 않으면 예외 발생
                throw new HttpParseException("Invalid request line: " + lines[0]);
            }
            HttpMethod method = HttpMethod.fromString(start[0]); // HTTP 메서드
            String path = start[1]; // 요청 경로
            String version = start[2]; // HTTP 버전

            // 3. 헤더 파싱
            HttpHeaders headers = new HttpHeaders(); // 헤더를 저장할 객체
            for (int i = 1; i < lines.length; i++) { // 헤더는 1번째 줄부터 시작
                String line = lines[i]; // 헤더 한 줄
                if (line.isEmpty()) { // 빈 줄이면 다음 줄로 넘어감
                    continue; // 빈 줄은 무시
                }
                String[] kv = line.split(":", 2); //key-value, kv[0]: key, kv[1]: value
                if (kv.length != 2) { // key-value 쌍이 아니면 예외 발생
                    throw new HttpParseException("Invalid header: " + line);
                }
                headers.addHeader(kv[0], kv[1]); // 헤더 추가
            }

            // 4. 바디 읽기
            int length = headers.getFirst("Content-Length")// Content-Length 헤더
                    .map(Integer::parseInt) // 정수로 변환
                    .orElse(0); // Content-Length가 없으면 0
            byte[] body = new byte[length]; // 바디를 저장할 바이트 배열
            int read = 0; // 읽은 바이트 수
            while (read < length) { // 바디를 다 읽을 때까지 반복
                int r = in.read(body, read, length - read); // 바디 읽기
                if (r == -1) { // EOF
                    break; // 반복문 종료
                }
                read += r; // 읽은 바이트 수 증가
            }
            if (read != length) { // 읽은 바이트 수가 Content-Length와 다르면 예외 발생
                throw new HttpParseException("Expected: " + length + "bytes, got " + read);
            }

            return new HttpRequest(method, path, version, headers, body); // HttpRequest 객체 생성

        } catch (IOException e) {
            throw new HttpParseException("Error parsing HTTP Request", e);
        }
    }
}