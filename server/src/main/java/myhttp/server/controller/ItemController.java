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

public class ItemController implements RequestHandler {
    private final ItemService itemService;

    public ItemController(ItemService service) {
        this.itemService = service;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        switch (request.getMethod()) {
            case GET:    return handleGet(request);
            case POST:   return handlePost(request);
            case PUT:    return handlePut(request);
            case HEAD:   return handleHead(request);
            default:
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.BAD_REQUEST)
                        .withBody("Method Not Allowed")
                        .build();
        }
    }

    private HttpResponse handleGet(HttpRequest request) {
        String path = request.getPath();
        if (path.matches("/items/\\d+")) {
            Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
            Item i = itemService.getItem(id);
            if (i == null) {
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.NOT_FOUND)
                        .withBody("Item not found")
                        .build();
            }
            String body = JsonUtil.toJson(i);
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "application/json")
                    .withBody(body)
                    .build();
        } else if (path.equals("/items")) {
            List<Item> list = itemService.getAllItems();
            String body = JsonUtil.toJson(list);
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK)
                    .addHeader("Content-Type", "application/json")
                    .withBody(body)
                    .build();
        } else {
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.NOT_FOUND)
                    .withBody("Not Found")
                    .build();
        }
    }

    private HttpResponse handlePost(HttpRequest request) {
        Item i = JsonUtil.fromJson(new String(request.getBody()), Item.class);
        Item created = itemService.createItem(i);
        String body = JsonUtil.toJson(created);
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CREATED)
                .addHeader("Content-Type", "application/json")
                .withBody(body)
                .build();
    }

    private HttpResponse handlePut(HttpRequest request) {
        String path = request.getPath();
        if (!path.matches("/items/\\d+")) {
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.BAD_REQUEST)
                    .withBody("Invalid path for PUT")
                    .build();
        }
        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        Item i = JsonUtil.fromJson(new String(request.getBody()), Item.class);
        i.setId(id);
        Item updated = itemService.updateItem(i);
        String body = JsonUtil.toJson(updated);
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.OK)
                .addHeader("Content-Type", "application/json")
                .withBody(body)
                .build();
    }

    private HttpResponse handleHead(HttpRequest req) {
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CONTINUE)
                .build();
    }
}
