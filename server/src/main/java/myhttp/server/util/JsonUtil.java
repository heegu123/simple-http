package myhttp.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
* JSON 변환 유틸리티 클래스
* */
public class JsonUtil {
    // Jackson ObjectMapper 인스턴스 생성
    private static final ObjectMapper mapper = new ObjectMapper();

    // JSON 변환 메서드(Serialization)
    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj); // 객체를 JSON 문자열로 변환
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    // JSON 역변환 메서드(Deserialization)
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz); // JSON 문자열을 객체로 변환
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON deserialization error", e);
        }
    }
}