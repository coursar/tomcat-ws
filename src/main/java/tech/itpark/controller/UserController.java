package tech.itpark.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import tech.itpark.bodyconverter.BodyConverter;
import tech.itpark.model.User;
import tech.itpark.service.UserService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class UserController {
  private final UserService service;
  private final List<BodyConverter> converters;

  public void getAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
    final var data = service.getAll();
    write(data, "application/json", response);
  }
  public void getById(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().write("getById");
  }
  public void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // 1. Подготовка данных для сервиса
    // 2. Вызов сервиса
    // 3. Обработка ответа
    final var dto = read(User.class, request);
    final var saved = service.save(dto);
    write(saved, "application/json", response);
  }
  public void deleteById(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().write("deleteById");
  }

  public <T> T read(Class<T> clazz, HttpServletRequest request) {
    for (final var converter : converters) {
      if (!converter.canRead(request.getContentType(), clazz)) {
        continue;
      }

      try {
        return converter.read(request.getReader(), clazz);
      } catch (IOException e) {
        e.printStackTrace();
        // TODO: convert to special exception
        throw new RuntimeException(e);
      }
    }
    // TODO: convert to special exception
    throw new RuntimeException("no converters support given content type");
  }

  public void write(Object data, String contentType, HttpServletResponse response) {
    for (final var converter : converters) {
      if (!converter.canWrite(contentType, data.getClass())) {
        continue;
      }

      try {
        response.setContentType(contentType);
        converter.write(response.getWriter(), data);
        return;
      } catch (IOException e) {
        e.printStackTrace();
        // TODO: convert to special exception
        throw new RuntimeException(e);
      }
    }
    // TODO: convert to special exception
    throw new RuntimeException("no converters support given content type");
  }
}
