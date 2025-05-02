package myhttp.common.model;

import java.util.Objects;

public enum HttpMethod {
    // HTTP 메서드
    GET, POST, PUT, HEAD;

    /*
    *HTTP 메서드 문자열을 HttpMethod enum으로 변환하는 메서드
    *
    * @param s  HTTP 메서드 문자열
    * return  HttpMethod enum
    */
    public static HttpMethod fromString(String s){
        // null 체크 및 공백 제거
        String method = Objects.requireNonNull(s, "HTTP method string cannot be null").trim();

        try{
            return HttpMethod.valueOf(method); // valueOf 메서드를 사용하여 문자열을 enum으로 변환
        }catch (IllegalArgumentException e){
            // 예외 처리
            throw new IllegalArgumentException("Invalid HTTP method: " + s, e);
        }
    }
}
