package ru.lab1.dealership.domain.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.IncompatibleComponentException;

class CarConfigurationTest {
  @Test
  void shouldThrowIfMissingRequiredComponent() {
    CarModel model = carModel();
    CarConfiguration configuration = new CarConfiguration(model);

    assertThrows(DomainValidationException.class, configuration::totalPrice);
  }

  @Test
  void shouldThrowIfOptionIncompatible() {
    CarModel model = carModel();
    ComponentOption option =
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa",
            BigDecimal.valueOf(110000),
            Set.of(UUID.randomUUID()));
    CarConfiguration configuration = new CarConfiguration(model);

    assertThrows(IncompatibleComponentException.class, () -> configuration.selectOption(option));
  }

  @Test
  void shouldCalculateTotalPrice() {
    CarModel model = carModel();
    UUID modelId = model.getId();
    CarConfiguration configuration = new CarConfiguration(model);
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

    assertEquals(BigDecimal.valueOf(4330000), configuration.totalPrice());
  }

  private static CarModel carModel() {
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
