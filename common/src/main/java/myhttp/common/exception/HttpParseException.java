package myhttp.common.exception;

public class HttpParseException extends Exception{

    public HttpParseException(String msg) {
        super(msg);
    }

    public HttpParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
