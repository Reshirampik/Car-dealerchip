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

class CarCatalogFilteringTest {

  @Test
  void shouldFilterByBrandAndColor() {
    var repo = new CarInStockRepository();
    var service = new CarCatalogService(repo);

    var bmwModel =
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

    var audiModel =
        new CarModel(
            UUID.randomUUID(),
            "Audi",
            "A4",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            190,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.AWD,
            new BigDecimal("3300000"));

    repo.save(new CarInStock(UUID.randomUUID(), bmwModel, "black", new BigDecimal("3600000")));
    repo.save(new CarInStock(UUID.randomUUID(), bmwModel, "white", new BigDecimal("3650000")));
    repo.save(new CarInStock(UUID.randomUUID(), audiModel, "black", new BigDecimal("3400000")));

    var filter =
        new CarFilter(
            null, null, "BMW", null, null, null, null, null, null, null, null, null, "black");

    var result = service.listAvailableCars(filter);

    assertEquals(1, result.size());
    assertEquals("BMW", result.get(0).getModel().getBrand());
    assertEquals("black", result.get(0).getColor());
  }

  @Test
  void shouldThrowIfModelWithoutBrand() {
    var repo = new CarInStockRepository();
    var service = new CarCatalogService(repo);

    var filter =
        new CarFilter(
            null, null, null, "320i", null, null, null, null, null, null, null, null, null);

    assertThrows(DomainValidationException.class, () -> service.listAvailableCars(filter));
  }

  @Test
  void shouldFilterByPriceRange() {
    var repo = new CarInStockRepository();
    var service = new CarCatalogService(repo);

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

    repo.save(new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3000000")));
    repo.save(new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("3600000")));
    repo.save(new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4000000")));

    var filter =
        new CarFilter(
            new BigDecimal("3200000"),
            new BigDecimal("3800000"),
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
            null);

    var result = service.listAvailableCars(filter);
    assertEquals(1, result.size());
    assertEquals(new BigDecimal("3600000"), result.get(0).getPrice());
  }
}
