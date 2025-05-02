package myhttp.common.model;

/*
* HTTP 메시지 기본 기능 명세
* */
public interface HttpMessage {
    String startLine(); // 시작 라인
    HttpHeaders getHeaders(); // 헤더
    byte[] getBody(); // 바디
    byte[] toByteArray(); // 바이트 배열로 변환
}
