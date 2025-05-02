package myhttp.common.parser;

import myhttp.common.exception.HttpParseException;
import myhttp.common.model.HttpHeaders;
import myhttp.common.model.HttpResponse;
import myhttp.common.model.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseParser implements HttpMessageParser<HttpResponse> {

    @Override
    public HttpResponse parse(InputStream in) throws HttpParseException {
        // 1. 헤더를 \r\n\r\n 나올 때까지 읽어들임
        try {
            ByteArrayOutputStream headerBuf = new ByteArrayOutputStream(); // 헤더를 저장할 버퍼
            int prev = 0; // 이전 바이트를 저장할 변수
            int curr; // 현재 바이트를 저장할 변수
            while ((curr = in.read()) != -1) { // 바이트를 읽어옴
                headerBuf.write(curr); // 읽어온 바이트를 버퍼에 저장
                if (prev == '\r' && curr == '\n') { // \CRLF를 찾음
                    byte[] b = headerBuf.toByteArray();// 버퍼를 바이트 배열로 변환
                    int len = b.length; // 배열의 길이를 구함
                    if (len >= 4 // 배열의 길이가 4 이상일 때
                            && b[len - 4] == '\r' // CRLF를 찾음
                            && b[len - 3] == '\n' // CRLF를 찾음
                            && b[len - 2] == '\r' // CRLF를 찾음
                            && b[len - 1] == '\n') { // CRLF를 찾음
                        break; // 헤더를 모두 읽었음
                    }
                }
                prev = curr; // 현재 바이트를 이전 바이트로 저장
            }
            String headerStr = headerBuf.toString(StandardCharsets.UTF_8); // 헤더를 문자열로 변환
            String[] lines = headerStr.split("\r\n"); // 헤더를 줄 단위로 나눔

            // 2) 상태 라인 파싱
            String[] start = lines[0].split(" ", 3); // 상태 라인을 공백으로 나눔
            if (start.length < 3) throw new HttpParseException("Invalid status line: " + lines[0]); // 상태 라인이 잘못됨
            String version = start[0]; // HTTP 버전
            int code = Integer.parseInt(start[1]); // 상태 코드
            String reason = start[2]; // 상태 메시지

            // 3) 헤더 파싱
            HttpHeaders headers = new HttpHeaders(); // 헤더를 저장할 객체
            for (int i = 1; i < lines.length; i++) { // 헤더를 줄 단위로 읽음
                String line = lines[i]; // 현재 줄
                if (line.isEmpty()) continue; // 빈 줄은 무시
                String[] kv = line.split(":", 2); // 키와 값을 나눔
                if (kv.length != 2) throw new HttpParseException("Invalid header: " + line); // 헤더가 잘못됨
                headers.addHeader(kv[0], kv[1]); // 헤더를 추가
            }

            // 4) 바디 읽기 (Content-Length 기준)
            int length = headers.getFirst("Content-Length") // 헤더에서 Content-Length를 찾음
                    .map(Integer::parseInt) // 정수로 변환
                    .orElse(0); // 없으면 0
            byte[] body = new byte[length]; // 바디를 저장할 배열
            int read = 0; // 읽은 바이트 수
            while (read < length) { // 바디를 모두 읽을 때까지
                int r = in.read(body, read, length - read); // 바디를 읽음
                if (r == -1) break; // 더 이상 읽을 바이트가 없음
                read += r; // 읽은 바이트 수를 증가
            }
            if (read != length) { // 모두 읽지 못했음
                throw new HttpParseException("Expected " + length + " bytes, got " + read); // 예외 발생
            }
            // 5) HttpResponse 객체 생성
            return new HttpResponse(version, HttpStatus.fromCode(code), reason, headers, body);
        } catch (IOException e) {
            throw new HttpParseException("Error parsing HTTP response", e);
        }
    }
}
