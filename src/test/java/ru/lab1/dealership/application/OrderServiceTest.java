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

class OrderServiceTest {

  @Test
  void shouldCreateOrderWithAutoAssignedManager() {
    var orderRepo = new CommonOrderRepository();
    var userRepo = new UserRepository();
    var carRepo = new CarInStockRepository();

    var manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepo.save(manager);

    var assign = new ManagerAssignmentService(userRepo, new Random(1));
    var service = new OrderService(orderRepo, userRepo, carRepo, assign);

    var client = new User(UUID.randomUUID(), "Ivan", UserRole.CLIENT);
    userRepo.save(client);

    var model =
        new CarModel(
            UUID.randomUUID(),
            "BMW",
            "320i",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            184,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("3500000"));

    var car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3600000"));
    carRepo.save(car);

    var order = service.createOrder(client.getId(), car.getId());

    assertEquals(CommonOrderStatus.CREATED, order.getStatus());
    assertEquals(client, order.getClient());
    assertEquals(UserRole.MANAGER, order.getManager().getRole());
  }
}
