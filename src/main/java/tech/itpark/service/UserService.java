package tech.itpark.service;

import lombok.RequiredArgsConstructor;
import tech.itpark.model.User;
import tech.itpark.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserService {
  private final UserRepository repository;

  public List<User> getAll() {
    return repository.getAll();
  }

  public Optional<User> getById() {
    return Optional.empty();
  }

  public User save(User user) {
    return user;
  }

  public void deleteById(long id) { }
}
