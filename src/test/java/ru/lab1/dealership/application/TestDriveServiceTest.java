package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class TestDriveServiceTest {
  @Test
  void shouldCreateTestDriveRequestOnlyForAllowedCar() {
    UserRepository userRepository = new UserRepository();
    User client = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    userRepository.save(client);

    CarInStockRepository carRepository = new CarInStockRepository();
    CarInStock car =
        new CarInStock(
            UUID.randomUUID(), mercedesModel(), "obsidian black", BigDecimal.valueOf(4690000));
    carRepository.save(car);

    TestDriveService service =
        new TestDriveService(new TestDriveRequestRepository(), userRepository, carRepository);
    service.addCarForTestDrive(car.getId());

    var request =
        service.createRequest(client.getId(), car.getId(), LocalDateTime.now().plusDays(1));

    assertEquals(car, request.getCar());
  }

  @Test
  void shouldRejectPastDate() {
    UserRepository userRepository = new UserRepository();
    User client = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    userRepository.save(client);

    CarInStockRepository carRepository = new CarInStockRepository();
    CarInStock car =
        new CarInStock(
            UUID.randomUUID(), mercedesModel(), "obsidian black", BigDecimal.valueOf(4690000));
    carRepository.save(car);

    TestDriveService service =
        new TestDriveService(new TestDriveRequestRepository(), userRepository, carRepository);
    service.addCarForTestDrive(car.getId());

    assertThrows(
        DomainValidationException.class,
        () -> service.createRequest(client.getId(), car.getId(), LocalDateTime.now().minusDays(1)));
  }

  private static CarModel mercedesModel() {
    return new CarModel(
        UUID.randomUUID(),
        "Mercedes-Benz",
        "C 200",
        BodyType.SEDAN,
        FuelType.GASOLINE,
        204,
        new BigDecimal("1.5"),
        GearboxType.AUTOMATIC,
        DriveType.RWD,
        BigDecimal.valueOf(4100000));
  }
}
