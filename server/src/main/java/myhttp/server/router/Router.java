package myhttp.server.router;

import myhttp.common.builder.HttpResponseBuilder;
import myhttp.common.model.HttpRequest;
import myhttp.common.model.HttpStatus;
import myhttp.server.controller.ItemController;
import myhttp.server.controller.UserController;
import myhttp.server.handler.RequestHandler;
import myhttp.server.repository.ItemRepository;
import myhttp.server.repository.UserRepository;
import myhttp.server.service.ItemService;
import myhttp.server.service.UserService;

import javax.persistence.EntityManagerFactory;

// 요청을 처리할 컨트롤러를 결정하는 라우터
public class Router {

    private final UserController userController;
    private final ItemController itemController;

    public Router(EntityManagerFactory emf) {
        UserService userService = new UserService(new UserRepository(emf));
        this.userController = new UserController(userService);
        ItemService itemService = new ItemService(new ItemRepository(emf));
        this.itemController = new ItemController(itemService);
    }

    public RequestHandler route(HttpRequest request) {
        String path = request.getPath();
        if (path.startsWith("/users")) { // /users로 시작하는 경로
            return userController; // UserController를 반환
        }
        if (path.startsWith("/items")) { // /items로 시작하는 경로
            return itemController; // ItemController를 반환
        }
        // 그 외의 경로는 404 Not Found 처리
        return req -> new HttpResponseBuilder()
                .withStatus(HttpStatus.NOT_FOUND)
                .withBody("Not Found")
                .build();
    }
}
