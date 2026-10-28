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
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class OrderServiceListTest {

  @Test
  void shouldListOrders() {
    var orderRepo = new CommonOrderRepository();
    var userRepo = new UserRepository();
    var carRepo = new CarInStockRepository();

    userRepo.save(new User(UUID.randomUUID(), "M1", UserRole.MANAGER));

    var service =
        new OrderService(
            orderRepo, userRepo, carRepo, new ManagerAssignmentService(userRepo, new Random(1)));

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

    var car1 = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3600000"));
    var car2 = new CarInStock(UUID.randomUUID(), model, "white", new BigDecimal("3650000"));

    carRepo.save(car1);
    carRepo.save(car2);

    service.createOrder(client.getId(), car1.getId());
    service.createOrder(client.getId(), car2.getId());

    assertEquals(2, service.listOrders().size());
  }
}
