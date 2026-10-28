package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.part.SparePart;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.SparePartRepository;

class WarehouseServiceTest {
  @Test
  void shouldCrudCarAndSparePart() {
    WarehouseService service =
        new WarehouseService(
            new CarModelRepository(), new CarInStockRepository(), new SparePartRepository());

    CarModel model = mercedesModel();
    service.createCarModel(model);
    service.updateCarModel(
        model.getId(),
        "Mercedes-Benz",
        "C 200 facelift",
        BodyType.SEDAN,
        FuelType.GASOLINE,
        204,
        new BigDecimal("1.5"),
        GearboxType.AUTOMATIC,
        DriveType.RWD,
        BigDecimal.valueOf(4150000));
    assertEquals("C 200 facelift", service.getCarModel(model.getId()).getModel());

    CarInStock car =
        new CarInStock(UUID.randomUUID(), model, "obsidian black", BigDecimal.valueOf(4690000));
    service.createCarInStock(car);
    service.updateCarInStock(car.getId(), "polar white", BigDecimal.valueOf(4720000));
    assertEquals("polar white", service.getCarInStock(car.getId()).getColor());

    SparePart part =
        new SparePart(
            UUID.randomUUID(), "Brake pads", BigDecimal.valueOf(24000), Set.of(model.getId()));
    service.createSparePart(part);
    service.updateSparePart(
        part.getId(), "Brake pads PRO", BigDecimal.valueOf(26000), Set.of(model.getId()));
    assertEquals("Brake pads PRO", service.getSparePart(part.getId()).getName());
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
