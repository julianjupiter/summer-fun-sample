package sample;

import io.summer.fun.HttpMethod;
import io.summer.fun.Route;
import io.summer.fun.RouteCollection;
import io.summer.fun.SummerFunApplication;

public class SampleApp {

    public static void main(String[] args) {
        SummerFunApplication app = new SummerFunApplication()
                .withContextPath("/app")    // Default is /
                .withPort(8083);            // Default is 8080

        // 1.
        app.addRoute(HttpMethod.GET, "/users", (request, response) -> {
            String users = "[{\"id\": 1, \"lastName\": \"Rizal\", \"firsName\": \"Jose\"}," +
                "{\"id\": 2, \"lastName\": \"Bonifacio\", \"firsName\": \"Andres\"}," +
                "{\"id\": 3, \"lastName\": \"Mabini\", \"firsName\": \"Apolinario\"}]";
            response.json(users);
        });

        // 2.
        app.addRoute(new Route(HttpMethod.GET, "/users/{id}", (request, response) -> {
            int id = Integer.parseInt(request.getPathParam("id"));
            if (id == 1) {
                String user = "{\"id\": 1, \"lastName\": \"Rizal\", \"firsName\": \"Jose\"}";
                response.json(user);
            } else if (id == 2) {
                String user = "{\"id\": 2, \"lastName\": \"Bonifacio\", \"firsName\": \"Andres\"}";
                response.json(user);
            } else if (id == 3) {
                String user = "{\"id\": 3, \"lastName\": \"Mabini\", \"firsName\": \"Apolinario\"}";
                response.json(user);
            } else {
                response.setStatus(404);
                String json = "{\"error\": {\"code\": 404, \"message\": \"Resource Not Found\"}}";
                response.json(json);
            }
        }));

        // 3.
        RouteCollection routes = new RouteCollection();

        Route home = new Route(HttpMethod.GET, "/home", (request, response) -> {
            response.html("<h1>Welcome home!</h1>");
        });

        Route users = new Route(HttpMethod.GET, "/hello", (request, response) -> {
            response.send("Hello!");
        });

        routes.add(home);
        routes.add(users);
        app.setRouteCollection(routes);

        // 4.
        app.get("/contact", (request, response) -> {
            response.html("<h2>Contact</h2>");
        });

        app.run(() -> {
            System.out.println("Application is running on port " + app.getPort());
            System.out.println("Press any key to stop the server...");
        });
    }

}
