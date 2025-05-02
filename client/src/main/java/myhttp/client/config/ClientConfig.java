package myhttp.client.config;

/*
    * HTTP 클라이언트 설정
    * 호스트와 포트를 포함
    */

public class ClientConfig {

    private final String host; // 서버 호스트
    private final int port; // 서버 포트

    // 생성자
    public ClientConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
