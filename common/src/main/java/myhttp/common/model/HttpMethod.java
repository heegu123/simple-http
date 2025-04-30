package myhttp.common.model;

import java.util.Objects;

public enum HttpMethod {
    GET, POST, PUT, HEAD;

    public static HttpMethod fromString(String s){

        String method = Objects.requireNonNull(s, "HTTP method string cannot be null").trim();

        try{
            return HttpMethod.valueOf(method);
        }catch (IllegalArgumentException e){
            //TODO : HTTP Method 없을 떄 예외처리 추가
            throw new IllegalArgumentException("Invalid HTTP method: " + s, e);
        }
    }
}
