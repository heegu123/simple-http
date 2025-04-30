package myhttp.common.model;

import java.util.*;

public class HttpHeaders {

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

        Objects.requireNonNull(name, "Header field name cannot be null");
        Objects.requireNonNull(value, "Header field value cannot be null");

        String key = name.trim();
        String val = value.trim();

        String existingKey = headers.keySet().stream()
                .filter(k -> k.equalsIgnoreCase(key))
                .findFirst()
                .orElse(key);

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

        Objects.requireNonNull(name, "Header field name cannot be null");
        Objects.requireNonNull(value, "Header field value cannot be null");

        String key = name.trim();
        String val = value.trim();

        headers.keySet().removeIf(k -> k.equalsIgnoreCase(key));

        headers.put(key, new ArrayList<>(Collections.singletonList(val)));
    }

    /*
    * 찾는 헤더 필드의 모든 값들 반환
    * */
    public List<String> getHeaders(String name) {

        if (name == null) {
            return Collections.emptyList();
        }

        String key = name.trim();

        return headers.entrySet().stream()
                .filter(e -> e.getKey().equalsIgnoreCase(key))
                .flatMap(e -> e.getValue().stream())
                .toList();
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

        StringBuilder sb = new StringBuilder();

        for (var entry : headers.entrySet()) {
            String key = entry.getKey();
            for (String val : entry.getValue()) {
                sb.append(key).append(": ").append(val).append("\r\n");
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
