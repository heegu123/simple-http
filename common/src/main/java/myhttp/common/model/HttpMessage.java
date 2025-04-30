package myhttp.common.model;

/*
* HTTP 메시지 기본 기능 명세
* */
public interface HttpMessage {
    String startLine();
    HttpHeaders getHeaders();
    byte[] getBody();
    byte[] toByteArray();
}
