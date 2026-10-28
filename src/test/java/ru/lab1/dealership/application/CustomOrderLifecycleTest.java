package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.config.ComponentOption;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.order.CustomOrderStatus;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class CustomOrderLifecycleTest {
  @Test
  void shouldCreateCustomOrderAndMoveThroughStatuses() {
    UserRepository userRepository = new UserRepository();
    User client = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Olga Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModelRepository carModelRepository = new CarModelRepository();
    CarModel model = mercedesModel();
    carModelRepository.save(model);

    CarConfiguration configuration = new CarConfiguration(model);
    UUID modelId = model.getId();
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            BigDecimal.valueOf(95000),
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "9G-TRONIC",
            BigDecimal.ZERO,
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "AMG heated",
            BigDecimal.valueOf(25000),
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            BigDecimal.valueOf(110000),
            Set.of(modelId)));

    CustomOrderService service =
        new CustomOrderService(
            new CustomOrderRepository(),
            userRepository,
            carModelRepository,
            new ManagerAssignmentService(userRepository, new Random(1)));

    var order = service.createOrder(client.getId(), configuration);
    assertEquals(CustomOrderStatus.CREATED, order.getStatus());

    service.approveByWarehouse(order.getId());
    service.moveToWaitingPayment(order.getId());
    service.markPaid(order.getId());
    service.moveToWaitingDelivery(order.getId());
    service.markReadyForPickup(order.getId());
    service.complete(order.getId());

    assertEquals(CustomOrderStatus.COMPLETED, service.getOrder(order.getId()).getStatus());
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
