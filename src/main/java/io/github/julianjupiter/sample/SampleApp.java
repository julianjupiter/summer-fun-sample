package io.github.julianjupiter.sample;

import summer.fun.*;
import summer.fun.http.HttpMethod;
import summer.fun.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SampleApp {

    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();

        ViewResolver viewResolver = new PebbleViewResolver();
        viewResolver.setPrefix("templates");
        viewResolver.setSuffix(".html");

        Configuration config = Configuration.builder()
                .withContextPath("/app")
                .withViewResolver(viewResolver)
                .build();

        SummerFun app = new SummerFun()
                .withConfiguration(config);

        // 1. http://localhost:8080/app/users
        app.addRoute(HttpMethod.GET, "/users", (request, response) -> {
            List<User> users = userRepository.findAll();
            response.json().send(users);
        });

        // 2. http://localhost:8080/app/users/{id}
        app.addRoute(new Route(HttpMethod.GET, "/users/{id}", (request, response) -> {
            int id = Integer.parseInt(request.getPathParam("id"));
            Optional<User> userOptional = userRepository.findById(id);
            User user = userOptional.orElse(null);
            if (user != null) {
                response.json().send(user);
            } else {
                Error error = new Error(404, "Resource Not Found");
                response.setStatus(HttpStatus.NOT_FOUND_404.getCode());
                response.json().send(error);
            }
        }));

        // 3. http://localhost:8080/app/user-list
        app.get("/user-list", (request, response) -> {
            List<User> users = userRepository.findAll();
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
