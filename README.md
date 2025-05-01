# myhttp

## 개요
- TCP 소켓 통신으로 직접 구현한 HTTP/1.1 서버·클라이언트
- Java 21, Gradle, JPA, MySQL
- GET, HEAD, POST, PUT 메서드 파싱·구성, HTTP/1.1 지속 연결(persistent connection) 지원

## 모듈 구조
```
myhttp/
├─ common/     # HTTP 메시지 모델·파서·빌더·유틸
├─ server/     # HTTP 서버, JPA 연동, Router→Controller 처리
└─ client/     # 콘솔 기반 HTTP 클라이언트
```

---

## common 모듈
**경로:** `common/src/main/java/myhttp/common`

### model/
- `HttpMethod.java` : GET, HEAD, POST, PUT enum, `fromString()` 매핑
- `HttpHeaders.java` : 헤더 저장(Map<String,List>), `toRawString()` 직렬화
- `HttpRequest.java` : 요청 모델(startLine(), toByteArray())
- `HttpResponse.java`: 응답 모델(startLine(), toByteArray())

### parser/
- `HttpRequestParser.java` : 바이트 레벨 `\r\n\r\n` 구분자로 헤더/바디 분리
- `HttpResponseParser.java`: 상태라인→헤더→바디 읽기, `Content-Length` 기반

### builder/
- `HttpRequestBuilder.java` : 빌더 패턴 요청 생성, 경로·메서드·헤더·본문
- `HttpResponseBuilder.java`: 빌더 패턴 응답 생성, 기본 헤더(Date, Server, Connection) 자동 추가

### util/
- `JsonUtil.java`
---

## server 모듈
**경로:** `server/`

### Application.java
- `ServerSocket` → ExecutorService 풀로 연결 분배
- `handleClient()`에서 요청 파싱→핸들러 호출→응답 전송→persistent connection 지원

### config/JpaConfig.java
- JPA `EntityManagerFactory` 싱글톤 설정

### entity/
- `User.java`, `Item.java` : JPA `@Entity` 매핑 클래스

### repository/
- CRUD 구현: `find()`, `findAll()`, `save()`, `update()`, `delete()`

### service/
- `UserService`, `ItemService` 

### handler/
- `RequestHandler.java` : `handle(HttpRequest)` 인터페이스

### router/
- `Router.java` : URI 패턴에 따라 `UserController` or `ItemController` 호출

### controller/
- `UserController.java`, `ItemController.java` : HTTP 메서드별 처리, JSON 변환, 상태 코드 설정

### util/JsonUtil.java
- Jackson `ObjectMapper` 래퍼: toJson(), fromJson()

---

## client 모듈
**경로:** `client/`

### config/ClientConfig.java
- 서버 호스트·포트 설정

### http/HttpClient.java
- 소켓 재사용 방식 persistent connection 지원
- `send()` 메서드: 요청 전송→응답 파싱→Connection 헤더에 따라 재연결

### util/JsonUtil.java
- Jackson 직렬화/역직렬화 유틸

### Application.java
- 콘솔 입력(한 줄 `METHOD PATH [BODY]`) → 요청 빌드·전송 → 응답 출력
- 디버그용 소켓 상태 출력(hashCode, isClosed)

---

## 실행 순서 예시
```bash
# 서버 실행
./gradlew :server:run

# 클라이언트 실행
./gradlew :client:run
Enter request (METHOD PATH [BODY]) or EXIT: GET /users
```

