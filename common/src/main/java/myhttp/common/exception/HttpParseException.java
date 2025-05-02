package myhttp.common.exception;

// HTTP 요청 메시지 파싱 중 발생하는 예외를 처리하기 위한 클래스
public class HttpParseException extends Exception{

    // 생성자, msg = 예외 메시지
    public HttpParseException(String msg) {
        super(msg);
    }

    // 생성자, msg = 예외 메시지, cause = 원인 예외
    public HttpParseException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
