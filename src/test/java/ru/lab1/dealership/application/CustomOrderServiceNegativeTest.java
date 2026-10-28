package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

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
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class CustomOrderServiceNegativeTest {
  @Test
  void shouldRejectNonClientCustomOrderCreation() {
    UserRepository userRepository = new UserRepository();
    CarModelRepository carModelRepository = new CarModelRepository();
    CustomOrderRepository customOrderRepository = new CustomOrderRepository();

    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    carModelRepository.save(model);

    CarConfiguration configuration = buildConfiguration(model);

    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    CustomOrderService service =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);

    assertThrows(
        DomainValidationException.class, () -> service.createOrder(manager.getId(), configuration));
  }

  private static CarConfiguration buildConfiguration(CarModel model) {
    CarConfiguration configuration = new CarConfiguration(model);
    UUID modelId = model.getId();

    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            new BigDecimal("95000"),
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
            "AMG steering wheel",
            new BigDecimal("25000"),
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            new BigDecimal("110000"),
            Set.of(modelId)));

    return configuration;
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
