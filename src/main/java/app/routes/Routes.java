package app.routes;

import app.controllers.DogController;
import io.javalin.apibuilder.EndpointGroup;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketOpen;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {
    private final DogController dogController;

    public Routes(DogController dogController) {
        this.dogController = dogController;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", ctx -> ctx.result("Hello Javalin World"));
            path("/dogs", () -> {
                get("/", dogController::getAll);
                post("/", dogController::create);
                get("/{id}", dogController::getById);
            });
        };
    };
}
