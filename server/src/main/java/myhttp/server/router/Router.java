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
        if (path.startsWith("/users")) {
            return userController;
        }
        if (path.startsWith("/items")) {
            return itemController;
        }
        return req -> new HttpResponseBuilder()
                .withStatus(HttpStatus.NOT_FOUND)
                .withBody("Not Found")
                .build();
    }
}
