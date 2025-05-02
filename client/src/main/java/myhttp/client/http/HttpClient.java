package myhttp.client.http;

import myhttp.client.config.ClientConfig;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.parser.HttpResponseParser;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

// * HTTP 클라이언트
// * HTTP 요청을 서버에 전송하고 응답을 받는 역할
// Closeable 인터페이스를 구현하여 자원 해제를 지원
public class HttpClient implements Closeable {

    private final ClientConfig config; // 클라이언트 설정
    private Socket socket; // 소켓
    private OutputStream out; // 출력 스트림
    private InputStream in; // 입력 스트림

    public HttpClient(ClientConfig config) {
        this.config = config;
        connect();
    }

    public Socket getSocket() {
        return socket;
    } // 소켓 getter

    // 소켓 연결
    private void connect() {
        try {
            socket = new Socket(config.getHost(), config.getPort()); // 소켓 생성
            out = socket.getOutputStream(); // 출력 스트림 생성
            in = socket.getInputStream(); // 입력 스트림 생성
        } catch (Exception e) {
            throw new HttpClientException("Cannot open Socket", e); // 소켓 생성 실패 시 예외 발생
        }
    }

    // HTTP 요청 전송 및 응답 수신
    public HttpResponse send(HttpRequest request) {
        try {
            // 1. 요청 전송
            out.write(request.toByteArray()); // 요청 바이트 배열 전송
            out.flush(); // 출력 스트림 플러시

            // 2. 응답 파싱
            HttpResponseParser parser = new HttpResponseParser(); // 응답 파서 생성
            HttpResponse response = parser.parse(in); // 응답 파싱

            // 3) 서버가 Connection: close 지시했으면 닫았다가 다시 연결
            String connectionHeader = response.getHeaders().getFirst("Connection").orElse("");
            if (connectionHeader.equalsIgnoreCase("close")) { // 서버가 연결 종료 지시
                close(); // 소켓 닫기
                connect(); // 재연결
            }

            return response; // 응답 반환

        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpClientException("HTTP Request failed", e);
        }
    }

    @Override
    public void close() { // 자원 해제
        try {
            if (socket != null && !socket.isClosed()) { // 소켓이 null이 아니고 닫히지 않았다면
                socket.close();
            }
        } catch (Exception ignored) {}
    }
}
