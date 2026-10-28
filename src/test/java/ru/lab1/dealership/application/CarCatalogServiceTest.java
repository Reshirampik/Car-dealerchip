package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;

class CarCatalogServiceTest {
  @Test
  void shouldFilterByBrandAndColor() {
    CarInStockRepository repository = new CarInStockRepository();
    CarCatalogService service = new CarCatalogService(repository);

    CarModel cClass = mercedesModel("C 200", BigDecimal.valueOf(4100000));
    CarModel eClass = mercedesModel("E 300", BigDecimal.valueOf(6900000));
    repository.save(
        new CarInStock(UUID.randomUUID(), cClass, "obsidian black", BigDecimal.valueOf(4690000)));
    repository.save(
        new CarInStock(UUID.randomUUID(), cClass, "polar white", BigDecimal.valueOf(4720000)));
    repository.save(
        new CarInStock(UUID.randomUUID(), eClass, "obsidian black", BigDecimal.valueOf(7350000)));

    var result =
        service.listAvailableCars(
            new CarFilter(
                null,
                null,
                "Mercedes-Benz",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                "obsidian black"));

    assertEquals(2, result.size());
  }

  @Test
  void shouldThrowIfModelWithoutBrand() {
    CarCatalogService service = new CarCatalogService(new CarInStockRepository());

    assertThrows(
        DomainValidationException.class,
        () ->
            service.listAvailableCars(
                new CarFilter(
                    null, null, null, "C 200", null, null, null, null, null, null, null, null,
                    null)));
  }

  @Test
  void shouldFilterByPriceRange() {
    CarInStockRepository repository = new CarInStockRepository();
    CarCatalogService service = new CarCatalogService(repository);

    CarModel model = mercedesModel("C 200", BigDecimal.valueOf(4100000));
    repository.save(
        new CarInStock(UUID.randomUUID(), model, "obsidian black", BigDecimal.valueOf(3900000)));
    repository.save(
        new CarInStock(UUID.randomUUID(), model, "obsidian black", BigDecimal.valueOf(4700000)));
    repository.save(
        new CarInStock(UUID.randomUUID(), model, "obsidian black", BigDecimal.valueOf(5200000)));

    var result =
        service.listAvailableCars(
            new CarFilter(
                BigDecimal.valueOf(4000000),
                BigDecimal.valueOf(5000000),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null));

    assertEquals(1, result.size());
    assertEquals(BigDecimal.valueOf(4700000), result.getFirst().getPrice());
  }

  private static CarModel mercedesModel(String name, BigDecimal basePrice) {
    return new CarModel(
        UUID.randomUUID(),
        "Mercedes-Benz",
        name,
        BodyType.SEDAN,
        FuelType.GASOLINE,
        204,
        new BigDecimal("1.5"),
        GearboxType.AUTOMATIC,
        DriveType.RWD,
        basePrice);
  }
}
