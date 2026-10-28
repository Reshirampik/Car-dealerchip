package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class ManagerAssignmentServiceTest {
  @Test
  void shouldThrowWhenNoManagersAvailable() {
    UserRepository userRepository = new UserRepository();
    ManagerAssignmentService service = new ManagerAssignmentService(userRepository, new Random(1));

    assertThrows(DomainValidationException.class, service::assignManager);
  }
}
