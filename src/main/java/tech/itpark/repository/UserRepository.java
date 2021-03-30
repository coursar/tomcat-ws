package tech.itpark.repository;

import lombok.RequiredArgsConstructor;
import tech.itpark.exception.DataAccessException;
import tech.itpark.model.User;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserRepository {
  // вместо Connection работает с DataSource, который может выдавать Connection по запросу
  private final DataSource ds;

  public void init() {
    try (
        final var connection = ds.getConnection();
        final var stmt = connection.createStatement();
    ) {
      stmt.execute("CREATE TABLE IF NOT EXISTS users ( id BIGSERIAL PRIMARY KEY, username TEXT NOT NULL UNIQUE );");
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }

  public List<User> getAll() {
    try (
        final var connection = ds.getConnection();
        final var stmt = connection.createStatement();
        final var rs = stmt.executeQuery("SELECT id, username FROM users");
    ) {
      List<User> result = new ArrayList<>();
      while (rs.next()) {
        result.add(new User(
            rs.getLong("id"),
            rs.getString("username")
        ));
      }
      return result;
    } catch (SQLException e) {
      throw new DataAccessException(e);
    }
  }
}
