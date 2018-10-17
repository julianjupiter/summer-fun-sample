package sample;

import io.summer.fun.HttpMethod;
import io.summer.fun.Route;
import io.summer.fun.RouteCollection;
import io.summer.fun.SummerFunApplication;
import sample.domain.User;
import sample.repository.UserRepository;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.List;
import java.util.Optional;

public class SampleApp {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        Jsonb jsonb = JsonbBuilder.create();

        SummerFunApplication app = new SummerFunApplication()
                .withContextPath("/app")    // Default is /
                .withPort(8083);            // Default is 8080

        // 1.
        app.addRoute("GET", "/users", (request, response) -> {
            List<User> users = userRepository.findAll();
            String userJson = jsonb.toJson(users);

            response.json(userJson);
        });

        // 2.
        app.addRoute(new Route(HttpMethod.GET, "/users/{id}", (request, response) -> {
            int id = Integer.parseInt(request.getPathParam("id"));
            Optional<User> userOptional = userRepository.findById(id);
            User user = userOptional.orElse(null);
            if (user != null) {
                response.json(jsonb.toJson(user));
            } else {
                response.setStatus(404);
                String json = "{\"error\": {\"code\": 404, \"message\": \"Resource Not Found\"}}";
                response.json(json);
            }
        }));

        // 3.
        RouteCollection routes = new RouteCollection();

        Route home = new Route(HttpMethod.GET, "/home", (request, response) -> {
            String content = "<!DOCTYPE html><html><head><title>Welcome home!</title></head><body>" +
                    "<h1>Welcome home!</h1>" +
                    "</body></html>";
            response.html(content);
        });

        Route users = new Route(HttpMethod.GET, "/hello", (request, response) -> {
            response.send("Hello!");
        });

        routes.add(home);
        routes.add(users);
        app.setRouteCollection(routes);

        // 4.
        app.get("/contact", (request, response) -> {
            String content = "<!DOCTYPE html><html><head><title>Welcome home!</title></head><body>" +
                    "<h1>Contact</h1>" +
                    "</body></html>";
            response.html(content);
        });

        app.run(() -> {
            System.out.println("Application is running on port " + app.getPort());
            System.out.println("Press any key to stop the server...");
        });
    }

}
