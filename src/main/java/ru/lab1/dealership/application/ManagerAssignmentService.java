package ru.lab1.dealership.application;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

public final class ManagerAssignmentService {
  private final UserRepository userRepository;
  private final Random random;

  public ManagerAssignmentService(UserRepository userRepository, Random random) {
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    this.random = Objects.requireNonNull(random, "random");
  }

  public User assignManager() {
    List<User> managers = userRepository.findAll().stream().filter(User::isManager).toList();
    if (managers.isEmpty()) {
      throw new DomainValidationException("No managers available for assignment.");
    }
    return managers.get(random.nextInt(managers.size()));
  }
}
