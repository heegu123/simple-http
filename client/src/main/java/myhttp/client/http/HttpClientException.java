package myhttp.client.http;

public class HttpClientException extends RuntimeException {

    public HttpClientException(String msg) {
        super(msg);
    }

    public HttpClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
