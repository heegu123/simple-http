// server/src/main/java/myhttp/server/controller/ItemController.java
package myhttp.server.controller;

import myhttp.common.builder.HttpResponseBuilder;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpResponse;
import myhttp.common.model.HttpStatus;
import myhttp.server.entity.Item;
import myhttp.server.handler.RequestHandler;
import myhttp.server.service.ItemService;
import myhttp.server.util.JsonUtil;

import java.util.List;

// HTTP 요청을 처리하는 컨트롤러 클래스
public class ItemController implements RequestHandler {
    private final ItemService itemService;

    public ItemController(ItemService service) {
        this.itemService = service;
    }

    // HTTP 요청을 처리하는 메서드
    @Override
    public HttpResponse handle(HttpRequest request) {
        switch (request.getMethod()) { // HTTP 메서드에 따라 분기
            case GET:    return handleGet(request); // GET 요청 처리
            case POST:   return handlePost(request); // POST 요청 처리
            case PUT:    return handlePut(request); // PUT 요청 처리
            case HEAD:   return handleHead(request); // HEAD 요청 처리
            default:
                // 지원하지 않는 메서드에 대한 응답
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.BAD_REQUEST)
                        .withBody("Method Not Allowed")
                        .build();
        }
    }

    // GET 요청을 처리하는 메서드
    private HttpResponse handleGet(HttpRequest request) {
        String path = request.getPath();
        if (path.matches("/items/\\d+")) { // 정규 표현식으로 ID 경로 확인
            Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1)); // ID 추출
            Item i = itemService.getItem(id); // ID로 아이템 조회
            if (i == null) { // 아이템이 없을 경우
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.NOT_FOUND) // 404 Not Found 응답
                        .withBody("Item not found") // 본문
                        .build();
            }
            String body = JsonUtil.toJson(i); // 아이템을 JSON으로 변환
            return new HttpResponseBuilder()  // HTTP 응답 빌더
                    .withStatus(HttpStatus.OK) // 200 OK 응답
                    .addHeader("Content-Type", "application/json") // 헤더 추가
                    .withBody(body)
                    .build();
        } else if (path.equals("/items")) { // 모든 아이템 조회
            List<Item> list = itemService.getAllItems(); // 모든 아이템 조회
            String body = JsonUtil.toJson(list); // 아이템 리스트를 JSON으로 변환
            return new HttpResponseBuilder() // HTTP 응답 빌더
                    .withStatus(HttpStatus.OK) // 200 OK 응답
                    .addHeader("Content-Type", "application/json") // 헤더 추가
                    .withBody(body)
                    .build();
        } else {
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.NOT_FOUND)
                    .withBody("Not Found")
                    .build();
        }
    }

    // POST 요청을 처리하는 메서드
    private HttpResponse handlePost(HttpRequest request) {
        Item i = JsonUtil.fromJson(new String(request.getBody()), Item.class); // JSON을 아이템으로 변환
        Item created = itemService.createItem(i); // 아이템 생성
        String body = JsonUtil.toJson(created); // 아이템을 JSON으로 변환
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CREATED) // 201 Created 응답
                .addHeader("Content-Type", "application/json") // 헤더 추가
                .withBody(body)
                .build();
    }

    // PUT 요청을 처리하는 메서드
    private HttpResponse handlePut(HttpRequest request) {
        String path = request.getPath();
        if (!path.matches("/items/\\d+")) { // 정규 표현식으로 ID 경로 확인, item 경로가 아닐 경우
            return new HttpResponseBuilder() // HTTP 응답 빌더
                    .withStatus(HttpStatus.BAD_REQUEST) // 400 Bad Request 응답
                    .withBody("Invalid path for PUT")
                    .build();
        }
        // ID 추출, /items/ 뒤에 있는 숫자
        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        // JSON을 아이템으로 변환
        Item i = JsonUtil.fromJson(new String(request.getBody()), Item.class);
        i.setId(id); // 아이템 ID 설정
        Item updated = itemService.updateItem(i); // 아이템 업데이트
        String body = JsonUtil.toJson(updated); // 아이템을 JSON으로 변환
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.OK) // 200 OK 응답
                .addHeader("Content-Type", "application/json") // 헤더 추가
                .withBody(body)
                .build();
    }
    // HEAD 요청을 처리하는 메서드
    private HttpResponse handleHead(HttpRequest req) {
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CONTINUE) // 100 Continue 응답
                .build();
    }
}
