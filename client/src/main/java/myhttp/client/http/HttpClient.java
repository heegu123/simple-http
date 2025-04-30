package myhttp.client.http;

import myhttp.client.config.ClientConfig;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.parser.HttpResponseParser;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HttpClient implements Closeable {

    private final ClientConfig config;
    private Socket socket;
    private OutputStream out;
    private InputStream in;

    public HttpClient(ClientConfig config) {
        this.config = config;
        connect();
    }

    public Socket getSocket() {
        return socket;
    }

    private void connect() {
        try {
            socket = new Socket(config.getHost(), config.getPort());
            out = socket.getOutputStream();
            in = socket.getInputStream();
        } catch (Exception e) {
            throw new HttpClientException("Cannot open Socket", e);
        }
    }

    public HttpResponse send(HttpRequest request) {
        try {
            // 1. 요청 전송
            out.write(request.toByteArray());
            out.flush();

            // 2. 응답 파싱
            HttpResponseParser parser = new HttpResponseParser();
            HttpResponse response = parser.parse(in);

            // 3) 서버가 Connection: close 지시했으면 닫았다가 다시 연결
            String connectionHeader = response.getHeaders().getFirst("Connection").orElse("");
            if (connectionHeader.equalsIgnoreCase("close")) {
                close();
                connect();
            }

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpClientException("HTTP Request failed", e);
        }
    }

    @Override
    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}
    }
}
