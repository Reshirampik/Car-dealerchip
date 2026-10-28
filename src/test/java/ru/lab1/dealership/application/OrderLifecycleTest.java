package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
import ru.lab1.dealership.domain.order.CommonOrderStatus;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class OrderLifecycleTest {
  @Test
  void shouldCreateAndMoveCommonOrderThroughStatuses() {
    UserRepository userRepository = new UserRepository();
    User client = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Olga Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarInStockRepository carRepository = new CarInStockRepository();
    CarInStock car =
        new CarInStock(
            UUID.randomUUID(), mercedesModel(), "obsidian black", BigDecimal.valueOf(4690000));
    carRepository.save(car);

    OrderService orderService =
        new OrderService(
            new CommonOrderRepository(),
            userRepository,
            carRepository,
            new ManagerAssignmentService(userRepository, new Random(1)));

    var order = orderService.createOrder(client.getId(), car.getId());
    assertEquals(CommonOrderStatus.CREATED, order.getStatus());

    orderService.approveByManager(order.getId());
    orderService.moveToWaitingPayment(order.getId());
    orderService.markPaid(order.getId());
    orderService.markReadyForPickup(order.getId());
    orderService.complete(order.getId());

    assertEquals(CommonOrderStatus.COMPLETED, orderService.getOrder(order.getId()).getStatus());
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
