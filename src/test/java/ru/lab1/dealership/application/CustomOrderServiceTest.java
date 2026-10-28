package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.*;
import ru.lab1.dealership.domain.config.*;
import ru.lab1.dealership.domain.order.CustomOrderStatus;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.*;

class CustomOrderServiceTest {

  @Test
  void shouldCreateCustomOrderWithAutoAssignedManager() {
    var orderRepo = new CustomOrderRepository();
    var userRepo = new UserRepository();
    var modelRepo = new CarModelRepository();

    var m1 = new User(UUID.randomUUID(), "M1", UserRole.MANAGER);
    var m2 = new User(UUID.randomUUID(), "M2", UserRole.MANAGER);
    userRepo.save(m1);
    userRepo.save(m2);

    var assign = new ManagerAssignmentService(userRepo, new Random(1));
    var service = new CustomOrderService(orderRepo, userRepo, modelRepo, assign);

    var client = new User(UUID.randomUUID(), "Ivan", UserRole.CLIENT);
    userRepo.save(client);

    var modelId = UUID.randomUUID();
    var model =
        new CarModel(
            modelId,
            "BMW",
            "320i",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            184,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("3500000"));

    modelRepo.save(model);

    var config = new CarConfiguration(model);

    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19 M-Sport",
            new BigDecimal("95000"),
            Set.of(modelId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "8AT",
            new BigDecimal("0"),
            Set.of(modelId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "M-Sport heated",
            new BigDecimal("25000"),
            Set.of(modelId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Dakota",
            new BigDecimal("110000"),
            Set.of(modelId)));

    var order = service.createOrder(client.getId(), config);

    assertEquals(CustomOrderStatus.CREATED, order.getStatus());
    assertEquals(client.getId(), order.getClient().getId());
    assertEquals(UserRole.MANAGER, order.getManager().getRole());
  }
}
