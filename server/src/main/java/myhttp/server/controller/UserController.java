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

public class UserController implements RequestHandler {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
                        .withRequestHeaders(request.getHeaders())
                        .withBody("Method Not Allowed")
                        .build();
        }
    }

    private HttpResponse handleGet(HttpRequest request) {

        String path = request.getPath();
        if (path.matches("/users/\\d+")) {
            Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
            User user = userService.getUser(id);
            if (user == null) {
                return new HttpResponseBuilder()
                        .withStatus(HttpStatus.NOT_FOUND)
                        .withBody("User Not Found")
                        .build();
            }
            String body = JsonUtil.toJson(user);
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK)
                    .withRequestHeaders(request.getHeaders())
                    .addHeader("Content-Type", "application/json")
                    .withBody(body)
                    .build();
        } else if (path.equals("/users")) {
            List<User> list = userService.getAllUsers();
            String body = JsonUtil.toJson(list);
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.OK)
                    .withRequestHeaders(request.getHeaders())
                    .addHeader("Content-Type", "application/json")
                    .withBody(body)
                    .build();
        } else {
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.NOT_FOUND)
                    .withRequestHeaders(request.getHeaders())
                    .withBody("Not Found")
                    .build();
        }
    }

    private HttpResponse handlePost(HttpRequest request) {
        String json = new String(request.getBody());
        User u = JsonUtil.fromJson(json, User.class);
        User created = userService.createUser(u);
        String body = JsonUtil.toJson(created);
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CREATED)
                .withRequestHeaders(request.getHeaders())
                .addHeader("Content-Type", "application/json")
                .withBody(body)
                .build();
    }

    private HttpResponse handlePut(HttpRequest request) {
        String path = request.getPath();
        if (!path.matches("/users/\\d+")) {
            return new HttpResponseBuilder()
                    .withStatus(HttpStatus.BAD_REQUEST)
                    .withRequestHeaders(request.getHeaders())
                    .withBody("Invalid path for PUT")
                    .build();
        }
        Long id = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
        User u = JsonUtil.fromJson(new String(request.getBody()), User.class);
        u.setId(id);
        User updated = userService.updateUser(u);
        String body = JsonUtil.toJson(updated);
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.OK)
                .withRequestHeaders(request.getHeaders())
                .addHeader("Content-Type", "application/json")
                .withBody(body)
                .build();
    }

    private HttpResponse handleHead(HttpRequest request) {
        return new HttpResponseBuilder()
                .withStatus(HttpStatus.CONTINUE)
                .withRequestHeaders(request.getHeaders())
                .build();
    }
}
