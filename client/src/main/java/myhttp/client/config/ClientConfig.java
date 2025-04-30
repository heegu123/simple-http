package myhttp.client.config;

public class ClientConfig {

    private final String host;
    private final int port;

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
