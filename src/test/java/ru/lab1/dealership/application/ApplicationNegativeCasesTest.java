package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.config.ComponentOption;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class ApplicationNegativeCasesTest {
  @Test
  void shouldRejectUnknownClientForCommonOrder() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));

    OrderService orderService =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);

    CarModel model = mercedesModel();
    carInStockRepository.save(
        new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000")));

    assertThrows(
        EntityNotFoundException.class,
        () -> orderService.createOrder(UUID.randomUUID(), UUID.randomUUID()));
  }

  @Test
  void shouldRejectUnknownModelForCustomOrder() {
    UserRepository userRepository = new UserRepository();
    CarModelRepository carModelRepository = new CarModelRepository();
    CustomOrderRepository customOrderRepository = new CustomOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    CustomOrderService customOrderService =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    CarConfiguration configuration = new CarConfiguration(model);
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            new BigDecimal("95000"),
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "9G-TRONIC",
            BigDecimal.ZERO,
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "AMG steering wheel",
            new BigDecimal("25000"),
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            new BigDecimal("110000"),
            Set.of(model.getId())));

    assertThrows(
        EntityNotFoundException.class,
        () -> customOrderService.createOrder(client.getId(), configuration));
  }

  @Test
  void shouldRejectMissingRequiredConfiguration() {
    CarModel model = mercedesModel();
    CarConfiguration configuration = new CarConfiguration(model);

    assertThrows(DomainValidationException.class, configuration::totalPrice);
  }

  @Test
  void shouldRejectPastTestDriveDate() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    TestDriveRequestRepository testDriveRequestRepository = new TestDriveRequestRepository();
    TestDriveService testDriveService =
        new TestDriveService(testDriveRequestRepository, userRepository, carInStockRepository);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    userRepository.save(client);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);
    testDriveService.addCarForTestDrive(car.getId());

    assertThrows(
        DomainValidationException.class,
        () ->
            testDriveService.createRequest(
                client.getId(), car.getId(), LocalDateTime.now().minusDays(1)));
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
        new BigDecimal("4100000"));
  }
}
