package ru.lab1.dealership.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.TestDriveRequestEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.TestDriveRequestRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;
import ru.lab1.dealership.web.dto.TestDriveResponse;

@Profile("order-service")
@Service
@Transactional
public class TestDriveService {
  private final TestDriveRequestRepository testDriveRequestRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;
  private final CurrentUserService currentUserService;

  public TestDriveService(
      TestDriveRequestRepository testDriveRequestRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository,
      CurrentUserService currentUserService) {
    this.testDriveRequestRepository = testDriveRequestRepository;
    this.userRepository = userRepository;
    this.carInStockRepository = carInStockRepository;
    this.currentUserService = currentUserService;
  }

  @Transactional(readOnly = true)
  public List<TestDriveResponse> list() {
    if (!currentUserService.hasAuthentication()) {
      return testDriveRequestRepository.findAllByRemovedFalse().stream()
          .map(this::toResponse)
          .toList();
    }
    UserEntity currentUser = currentUserService.currentUser();
    if (currentUser.getRole() == UserRole.CLIENT) {
      return testDriveRequestRepository
          .findAllByRemovedFalseAndClient_Id(currentUser.getId())
          .stream()
          .map(this::toResponse)
          .toList();
    }
    return testDriveRequestRepository.findAllByRemovedFalse().stream()
        .map(this::toResponse)
        .toList();
  }

  public TestDriveResponse create(TestDriveRequestDto request) {
    if (request == null || request.carId() == null || request.startDateTime() == null) {
      throw new DomainValidationException("Invalid test drive request.");
    }
    if (request.startDateTime().isBefore(Instant.now())) {
      throw new DomainValidationException("Test drive date must be in the future.");
    }
    UserEntity client = resolveClient(request.clientId());
    if (client.getRole() != UserRole.CLIENT
        && client.getRole() != UserRole.ADMIN
        && client.getRole() != UserRole.SYSTEM_ADMIN) {
      throw new DomainValidationException("Only client can request a test drive.");
    }
    CarInStockEntity car =
        carInStockRepository
            .findByIdAndRemovedFalse(request.carId())
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", request.carId()));
    if (!car.isAvailableForTestDrive()) {
      throw new DomainValidationException("Car is not available for test drive.");
    }
    TestDriveRequestEntity entity = new TestDriveRequestEntity();
    entity.setClient(client);
    entity.setCar(car);
    entity.setStartDateTime(request.startDateTime());
    return toResponse(testDriveRequestRepository.save(entity));
  }

  public void allowCar(UUID carId) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(carId)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", carId));
    entity.setAvailableForTestDrive(true);
    carInStockRepository.save(entity);
  }

  public void disallowCar(UUID carId) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(carId)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", carId));
    entity.setAvailableForTestDrive(false);
    carInStockRepository.save(entity);
  }

  private UserEntity resolveClient(UUID requestedClientId) {
    if (currentUserService.hasAuthentication()) {
      UserEntity currentUser = currentUserService.currentUser();
      if (currentUser.getRole() == UserRole.CLIENT
          || currentUser.getRole() == UserRole.ADMIN
          || currentUser.getRole() == UserRole.SYSTEM_ADMIN) {
        return currentUser;
      }
    }
    if (requestedClientId != null) {
      return userRepository
          .findByIdAndRemovedFalse(requestedClientId)
          .orElseThrow(EntityLookupSupport.notFound("User", requestedClientId));
    }
    return currentUserService.currentUser();
  }

  private TestDriveResponse toResponse(TestDriveRequestEntity entity) {
    return new TestDriveResponse(
        entity.getId(),
        entity.getClient().getId(),
        entity.getCar().getId(),
        entity.getStartDateTime(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
