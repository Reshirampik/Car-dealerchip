package ru.lab1.dealership.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.part.SparePart;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.SparePartRepository;

public class WarehouseService {
  protected final CarModelRepository carModelRepository;
  protected final CarInStockRepository carInStockRepository;
  protected final SparePartRepository sparePartRepository;

  public WarehouseService(
      CarModelRepository carModelRepository,
      CarInStockRepository carInStockRepository,
      SparePartRepository sparePartRepository) {
    this.carModelRepository = Objects.requireNonNull(carModelRepository, "carModelRepository");
    this.carInStockRepository =
        Objects.requireNonNull(carInStockRepository, "carInStockRepository");
    this.sparePartRepository = Objects.requireNonNull(sparePartRepository, "sparePartRepository");
  }

  public CarModel createCarModel(CarModel model) {
    return carModelRepository.save(model);
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
    CarModel carModel = getCarModel(id);
    carModel.update(
        brand,
        model,
        bodyType,
        fuelType,
        enginePowerHp,
        engineVolumeLiters,
        gearboxType,
        driveType,
        basePrice);
    return carModelRepository.save(carModel);
  }

  public CarModel getCarModel(UUID id) {
    return carModelRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Car model not found: " + id));
  }

  public List<CarModel> listCarModels() {
    return carModelRepository.findAll();
  }

  public void deleteCarModel(UUID id) {
    carModelRepository.delete(id);
  }

  public CarInStock createCarInStock(CarInStock car) {
    return carInStockRepository.save(car);
  }

  public CarInStock updateCarInStock(UUID id, String color, BigDecimal price) {
    CarInStock car = getCarInStock(id);
    car.update(color, price);
    return carInStockRepository.save(car);
  }

  public CarInStock getCarInStock(UUID id) {
    return carInStockRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Car in stock not found: " + id));
  }

  public List<CarInStock> listCarInStock() {
    return carInStockRepository.findAll();
  }

  public void deleteCarInStock(UUID id) {
    carInStockRepository.delete(id);
  }

  public SparePart createSparePart(SparePart sparePart) {
    return sparePartRepository.save(sparePart);
  }

  public SparePart updateSparePart(
      UUID id, String name, BigDecimal price, Set<UUID> compatibleIds) {
    SparePart part = getSparePart(id);
    part.update(name, price, compatibleIds);
    return sparePartRepository.save(part);
  }

  public SparePart getSparePart(UUID id) {
    return sparePartRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Spare part not found: " + id));
  }

  public List<SparePart> listSpareParts() {
    return sparePartRepository.findAll();
  }

  public void deleteSparePart(UUID id) {
    sparePartRepository.delete(id);
  }
}
