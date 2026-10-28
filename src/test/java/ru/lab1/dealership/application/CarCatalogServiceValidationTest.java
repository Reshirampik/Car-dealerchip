package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;

class CarCatalogServiceValidationTest {
  @Test
  void shouldRejectInvalidPriceRange() {
    CarCatalogService service = new CarCatalogService(new CarInStockRepository());

    assertThrows(
        DomainValidationException.class,
        () ->
            service.listAvailableCars(
                new CarFilter(
                    BigDecimal.valueOf(5000000),
                    BigDecimal.valueOf(4000000),
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
                    null)));
  }

  @Test
  void shouldRejectModelWithoutBrand() {
    CarCatalogService service = new CarCatalogService(new CarInStockRepository());

    assertThrows(
        DomainValidationException.class,
        () ->
            service.listAvailableCars(
                new CarFilter(
                    null, null, null, "C 200", null, null, null, null, null, null, null, null,
                    null)));
  }
}
