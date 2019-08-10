package io.github.julianjupiter.sample;

import summer.fun.http.HttpMethod;
import summer.fun.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import summer.fun.PebbleViewResolver;
import summer.fun.Route;
import summer.fun.SummerFun;
import summer.fun.ViewResolver;
import summer.fun.config.Configuration;
import summer.fun.config.ConfigurationBuilder;

/**
 * 
 * @author julian
 */
public class Application {

    public static void main(String[] args) {
        Supplier<UserRepository> userRepositorySupplier = () -> new UserRepository();

        ViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("templates");
        viewResolver.setSuffix(".html");

        Configuration config = ConfigurationBuilder.newBuilder()
                .withContextPath("/app")
                .withViewResolver(viewResolver)
                .build();

        SummerFun app = new SummerFun()
                .withConfiguration(config);

        // 1. http://localhost:7000/app/users
        app.addRoute(HttpMethod.GET, "/users", (request, response) -> {
            List<User> users = userRepositorySupplier.get().findAll();
            response.json().send(users);
        });

        // 2. http://localhost:7000/app/users/{id}
        app.addRoute(new Route(HttpMethod.GET, "/users/{id}", (request, response) -> {
            int id = Integer.parseInt(request.getPathParam("id"));
            Optional<User> userOptional = userRepositorySupplier.get().findById(id);
            User user = userOptional.orElse(null);
            if (user != null) {
                response.json().send(user);
            } else {
                Error error = new Error(404, "Resource Not Found");
                response.setStatus(HttpStatus.NOT_FOUND_404.getCode());
                response.json().send(error);
            }
        }));

        // 3. http://localhost:7000/app/user-list
        app.get("/user-list", (request, response) -> {
            List<User> users = userRepositorySupplier.get().findAll();
            String title = "Users";
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", title);
            attributes.put("users", users);
            response.render("users", attributes);
        });

        app.run(() -> {
            System.out.println("Application is running on port " + config.getPort());
            System.out.println("Press any key to stop the server...");
        });
    }

}
