package ru.lab1.dealership.application;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

public final class TestDriveService {
  private final TestDriveRequestRepository testDriveRequestRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;
  private final Set<UUID> allowedCarIds = new LinkedHashSet<>();

  public TestDriveService(
      TestDriveRequestRepository testDriveRequestRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository) {
    this.testDriveRequestRepository =
        Objects.requireNonNull(testDriveRequestRepository, "testDriveRequestRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    this.carInStockRepository =
        Objects.requireNonNull(carInStockRepository, "carInStockRepository");
  }

  public CarInStock addCarForTestDrive(UUID carId) {
    CarInStock car =
        carInStockRepository
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
    allowedCarIds.add(carId);
    return car;
  }

  public void removeCarFromTestDrive(UUID carId) {
    allowedCarIds.remove(carId);
  }

  public List<CarInStock> listCarsForTestDrive() {
    return carInStockRepository.findAll().stream()
        .filter(car -> allowedCarIds.contains(car.getId()))
        .toList();
  }

  public TestDriveRequest createRequest(UUID clientId, UUID carId, LocalDateTime startDateTime) {
    User client =
        userRepository
            .findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + clientId));
    if (!client.isClient()) {
      throw new DomainValidationException("Only a client can request a test drive.");
    }
    if (startDateTime == null) {
      throw new DomainValidationException("startDateTime cannot be null.");
    }
    if (startDateTime.isBefore(LocalDateTime.now())) {
      throw new DomainValidationException("Test drive cannot start in the past.");
    }
    if (!allowedCarIds.contains(carId)) {
      throw new DomainValidationException("Car is not available for test drive.");
    }
    CarInStock car =
        carInStockRepository
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
    return testDriveRequestRepository.save(
        new TestDriveRequest(UUID.randomUUID(), client, car, startDateTime));
  }

  public List<TestDriveRequest> listRequests() {
    return testDriveRequestRepository.findAll();
  }

  public TestDriveRequest getRequest(UUID requestId) {
    return testDriveRequestRepository
        .findById(requestId)
        .orElseThrow(
            () -> new EntityNotFoundException("Test drive request not found: " + requestId));
  }

  public void deleteRequest(UUID requestId) {
    testDriveRequestRepository.delete(requestId);
  }
}
