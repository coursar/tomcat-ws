package tech.itpark.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tech.itpark.bodyconverter.GsonBodyConverter;
import tech.itpark.controller.UserController;
import tech.itpark.exception.InitializationException;
import tech.itpark.http.Handler;
import tech.itpark.repository.UserRepository;
import tech.itpark.service.UserService;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Servlet
public class FrontController extends HttpServlet {
  private Map<String, Map<String, Handler>> routes;
  private final Handler notFoundHandler = (request, response) -> response.sendError(404, "Page not found");

  // init
  // destroy
  // service
  @Override
  public void init() {
    // JNDI:
    // https://docs.oracle.com/javase/tutorial/jndi/overview/index.html
    // https://tomcat.apache.org/tomcat-10.0-doc/jndi-resources-howto.html
    // https://tomcat.apache.org/tomcat-10.0-doc/jndi-datasource-examples-howto.html
    try {
      // В JNDI можно регистрировать не только DataSource, но для простоты обойдёмся только им
      final var cxt = new InitialContext();
      final var ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/db");
      // ручная сборка зависимостей
      final var repository = new UserRepository(ds);
      final var service = new UserService(repository);
      final var controller = new UserController(
          service,
          List.of(new GsonBodyConverter(new Gson()))
      );
      // TODO: builder
      routes = Map.of(
          "GET", Map.of(
              "/api/getUsers", controller::getAll,
              "/api/getUserById", controller::getById
          ),
          "POST", Map.of(
              "/api/saveUser", controller::save
          ),
          "DELETE", Map.of(
              "/api/deleteUserById", controller::deleteById
          )
      );

      repository.init();
    } catch (Exception e) {
      throw new InitializationException(e);
    }
  }

  @Override // in multiple threads
  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException {
    final var path = request.getServletPath(); // FIXME: RTFM
    final var method = request.getMethod();

    final var handler = Optional.ofNullable(routes.get(method))
        .map(o -> o.get(path))
        .orElseGet(() -> notFoundHandler);

    try {
      handler.handle(request, response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
