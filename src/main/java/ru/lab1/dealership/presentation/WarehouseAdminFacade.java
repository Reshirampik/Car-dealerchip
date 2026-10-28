package ru.lab1.dealership.presentation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.application.CarCatalogService;
import ru.lab1.dealership.application.CarFilter;
import ru.lab1.dealership.application.WarehouseService;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.part.SparePart;

public final class WarehouseAdminFacade {
  private final WarehouseService warehouseService;
  private final CarCatalogService carCatalogService;

  public WarehouseAdminFacade(
      WarehouseService warehouseService, CarCatalogService carCatalogService) {
    this.warehouseService = Objects.requireNonNull(warehouseService, "warehouseService");
    this.carCatalogService = Objects.requireNonNull(carCatalogService, "carCatalogService");
  }

  public CarModel createCarModel(CarModel model) {
    return warehouseService.createCarModel(model);
  }

  public CarModel updateCarModel(
      UUID id,
      String brand,
      String model,
      BodyType bodyType,
      FuelType fuelType,
      int enginePowerHp,
      BigDecimal engineVolumeLiters,
      GearboxType gearboxType,
      DriveType driveType,
      BigDecimal basePrice) {
    return warehouseService.updateCarModel(
        id,
        brand,
        model,
        bodyType,
        fuelType,
        enginePowerHp,
        engineVolumeLiters,
        gearboxType,
        driveType,
        basePrice);
  }

  public List<CarModel> listCarModels() {
    return warehouseService.listCarModels();
  }

  public CarInStock createCarInStock(CarInStock car) {
    return warehouseService.createCarInStock(car);
  }

  public CarInStock updateCarInStock(UUID id, String color, BigDecimal price) {
    return warehouseService.updateCarInStock(id, color, price);
  }

  public List<CarInStock> listCarInStock() {
    return warehouseService.listCarInStock();
  }

  public SparePart createSparePart(SparePart part) {
    return warehouseService.createSparePart(part);
  }

  public SparePart updateSparePart(
      UUID id, String name, BigDecimal price, Set<UUID> compatibleIds) {
    return warehouseService.updateSparePart(id, name, price, compatibleIds);
  }

  public List<SparePart> listSpareParts() {
    return warehouseService.listSpareParts();
  }

  public List<CarInStock> viewCars(CarFilter filter) {
    return carCatalogService.listAvailableCars(filter);
  }
}
