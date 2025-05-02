package myhttp.server.controller;

import myhttp.common.builder.HttpResponseBuilder;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.model.HttpStatus;
import myhttp.server.entity.User;
import myhttp.server.handler.RequestHandler;
import myhttp.server.service.UserService;
import myhttp.server.util.JsonUtil;

import java.util.List;

// HTTP 요청을 처리하는 컨트롤러 클래스
public class UserController implements RequestHandler {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // HTTP 요청을 처리하는 메서드
    @Override
    public HttpResponse handle(HttpRequest request) {
        switch (request.getMethod()) { //
            case GET:    return handleGet(request); // GET 요청 처리
            case POST:   return handlePost(request); // POST 요청 처리
            case PUT:    return handlePut(request); // PUT 요청 처리
            case HEAD:   return handleHead(request); // HEAD 요청 처리
            default: // 지원하지 않는 메서드에 대한 응답
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request 응답
                        .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                        .withBody("Method Not Allowed") // 본문
                        .build();
        }
    }

    // GET 요청을 처리하는 메서드
    private HttpResponse handleGet(HttpRequest request) {

        String path = request.getPath();
        if (path.matches("/users/\\d+")) { // 정규 표현식으로 ID 경로 확인
            Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1)); // ID 추출
            User user = userService.getUser(id); // ID로 사용자 조회
            if (user == null) { // 사용자가 없을 경우
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.NOT_FOUND) // 404 Not Found 응답
                        .withBody("User Not Found") // 본문
                        .build();
            }
            String body = JsonUtil.toJson(user); // 사용자를 JSON으로 변환
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK) // 200 OK 응답
                    .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                    .addHeader("Content-Type", "application/json") // 헤더 추가
                    .withBody(body)
                    .build();
        } else if (path.equals("/users")) { // 모든 사용자 조회
            List<User> list = userService.getAllUsers(); // 모든 사용자 조회
            String body = JsonUtil.toJson(list); // 사용자 리스트를 JSON으로 변환
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK) // 200 OK 응답
                    .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                    .addHeader("Content-Type", "application/json") // 헤더 추가
                    .withBody(body)
                    .build();
        } else { // 잘못된 경로
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.NOT_FOUND) // 404 Not Found 응답
                    .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                    .withBody("Not Found")
                    .build();
        }
    }

    // POST 요청을 처리하는 메서드
    private HttpResponse handlePost(HttpRequest request) {
        String json = new String(request.getBody()); // 요청 본문을 문자열로 변환
        User u = JsonUtil.fromJson(json, User.class); // JSON을 사용자로 변환
        User created = userService.createUser(u); // 사용자 생성
        String body = JsonUtil.toJson(created); // 사용자를 JSON으로 변환
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CREATED) // 201 Created 응답
                .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                .addHeader("Content-Type", "application/json") // 헤더 추가
                .withBody(body)
                .build();
    }

    // PUT 요청을 처리하는 메서드
    private HttpResponse handlePut(HttpRequest request) {
        String path = request.getPath(); // 요청 경로
        if (!path.matches("/users/\\d+")) { // 정규 표현식으로 ID 경로 확인, user 경로가 아닐 경우
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request 응답
                    .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                    .withBody("Invalid path for PUT") // 본문
                    .build();
        }
        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));// ID 추출
        User u = JsonUtil.fromJson(new String(request.getBody()), User.class); // JSON을 사용자로 변환
        u.setId(id); // 사용자 ID 설정
        User updated = userService.updateUser(u); // 사용자 업데이트
        String body = JsonUtil.toJson(updated); // 사용자를 JSON으로 변환
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.OK) // 200 OK 응답
                .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                .addHeader("Content-Type", "application/json")
                .withBody(body)
                .build();
    }

    // HEAD 요청을 처리하는 메서드
    private HttpResponse handleHead(HttpRequest request) {
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CONTINUE) // 100 Continue 응답
                .withRequestHeaders(request.getHeaders()) // 요청 헤더 포함
                .build();
    }
}
