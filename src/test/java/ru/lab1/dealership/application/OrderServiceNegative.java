package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Random;
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
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class OrderServiceNegativeTest {
  @Test
  void shouldRejectNonClientOrderCreation() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();

    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    User warehouseAdmin = new User(UUID.randomUUID(), "Warehouse", UserRole.WAREHOUSE_ADMIN);
    userRepository.save(manager);
    userRepository.save(warehouseAdmin);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    OrderService service =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);

    assertThrows(
        DomainValidationException.class,
        () -> service.createOrder(warehouseAdmin.getId(), car.getId()));
  }

  @Test
  void shouldRejectOrderWhenNoManagersExist() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    userRepository.save(client);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    OrderService service =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);

    assertThrows(
        DomainValidationException.class, () -> service.createOrder(client.getId(), car.getId()));
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
