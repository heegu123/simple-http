package myhttp.common.model;

import java.util.*;

public class HttpHeaders {
    // HTTP 헤더 필드 이름과 값들을 저장하는 Map
    private final Map<String, List<String>> headers = new LinkedHashMap<>();

    /**
     * HTTP 헤더 필드에 값 추가
     * 동일한 이름의 헤더가 이미 존재할 경우, 해당 헤더 필드에 값을 추가
     * 헤더 필드 이름은 대소문자를 구분하지 않고, 공백은 자동으로 제거.
     *
     * @param name  추가할 헤더 필드 이름 - "Content-Type"등
     * @param value 헤더 필드에 추가할 값
     * @throws NullPointerException name, value가 null인 경우
     */
    public void addHeader(String name, String value) {

        // null 체크
        Objects.requireNonNull(name, "Header field name cannot be null");
        Objects.requireNonNull(value, "Header field value cannot be null");

        String key = name.trim(); //공백 제거
        String val = value.trim(); // 공백 제거

        String existingKey = headers.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(key)) // 대소문자 구분하지 않음
                .findFirst() // 처음 찾은 키
                .orElse(key); // 없으면 key로 사용
        // 기존 키가 없으면 새로 추가
        headers.computeIfAbsent(existingKey, k -> new ArrayList<>()).add(val);
    }

    /**
     * HTTP 헤더 필드의 값을 설정.
     * 동일한 이름의 헤더 필드가 이미 존재할 경우, 해당 필드의 기존 값을 모두 제거
     * @param name  설정할 헤더 필드 이름
     * @param value 설정할 헤더 필드 값
     * @throws NullPointerException name, value가 null인 경우
     */
    public void setHeader(String name, String value) {
        // null 체크
        Objects.requireNonNull(name, "Header field name cannot be null");
        // null 체크
        Objects.requireNonNull(value, "Header field value cannot be null");

        String key = name.trim(); // 공백 제거
        String val = value.trim(); // 공백 제거

        headers.keySet().removeIf(k -> k.equalsIgnoreCase(key)); // 대소문자 구분하지 않음

        headers.put(key, new ArrayList<>(Collections.singletonList(val))); // 새로 추가
    }

    /*
    * 찾는 헤더 필드의 모든 값들 반환
    * */
    public List<String> getHeaders(String name) {
        // null 체크
        if (name == null) {
            return Collections.emptyList();
        }
        // 공백 제거
        String key = name.trim();

        return headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key)) // 대소문자 구분하지 않음
                .flatMap(e -> e.getValue().stream()) // 값들만 가져옴
                .toList(); // 리스트로 변환
    }


    /*
     * 찾는 헤더 필드의 첫번째 값 반환
     * */
    public Optional<String> getFirst(String name) {

        List<String> vals = getHeaders(name);
        return vals.isEmpty() ? Optional.empty() : Optional.of(vals.get(0));
    }

    /**
     * 모든 헤더를 HTTP Raw 형식으로 반환
     */
    public String toRawString() {

        StringBuilder sb = new StringBuilder(); // StringBuilder 사용

        for (var entry : headers.entrySet()) {
            String key = entry.getKey(); // 헤더 필드 이름
            // 반복문을 통해 헤더 필드의 모든 값들을 가져옴
            for (String val : entry.getValue()) {
                sb.append(key).append(": ").append(val).append("\r\n");
            }
        }
        sb.append("\r\n"); // 헤더와 바디 구분을 위한 CRLF 추가
        return sb.toString(); // StringBuilder를 String으로 변환하여 반환
    }
}
