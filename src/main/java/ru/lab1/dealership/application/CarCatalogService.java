package ru.lab1.dealership.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;

public final class CarCatalogService {
  private final CarInStockRepository carInStockRepository;

  public CarCatalogService(CarInStockRepository carInStockRepository) {
    this.carInStockRepository =
        Objects.requireNonNull(carInStockRepository, "carInStockRepository");
  }

  public List<CarInStock> listAvailableCars(CarFilter filter) {
    if (filter == null) {
      return carInStockRepository.findAll();
    }
    validateFilter(filter);
    return carInStockRepository.findAll().stream().filter(car -> matches(car, filter)).toList();
  }

  public CarInStock getCar(UUID carId) {
    return carInStockRepository
        .findById(carId)
        .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
  }

  private static void validateFilter(CarFilter filter) {
    if (filter.model() != null && filter.brand() == null) {
      throw new DomainValidationException("Model filter requires brand filter.");
    }
    if (filter.minPrice() != null
        && filter.maxPrice() != null
        && filter.minPrice().compareTo(filter.maxPrice()) > 0) {
      throw new DomainValidationException("minPrice cannot be greater than maxPrice.");
    }
    if (filter.minEnginePowerHp() != null
        && filter.maxEnginePowerHp() != null
        && filter.minEnginePowerHp() > filter.maxEnginePowerHp()) {
      throw new DomainValidationException(
          "minEnginePowerHp cannot be greater than maxEnginePowerHp.");
    }
    if (filter.minEngineVolumeLiters() != null
        && filter.maxEngineVolumeLiters() != null
        && filter.minEngineVolumeLiters().compareTo(filter.maxEngineVolumeLiters()) > 0) {
      throw new DomainValidationException(
          "minEngineVolumeLiters cannot be greater than maxEngineVolumeLiters.");
    }
  }

  private static boolean matches(CarInStock car, CarFilter filter) {
    var model = car.getModel();

    if (!matchesRange(car.getPrice(), filter.minPrice(), filter.maxPrice())) {
      return false;
    }
    if (filter.brand() != null && !model.getBrand().equalsIgnoreCase(filter.brand())) {
      return false;
    }
    if (filter.model() != null && !model.getModel().equalsIgnoreCase(filter.model())) {
      return false;
    }
    if (filter.bodyType() != null && model.getBodyType() != filter.bodyType()) {
      return false;
    }
    if (filter.fuelType() != null && model.getFuelType() != filter.fuelType()) {
      return false;
    }
    if (!matchesRange(
        model.getEnginePowerHp(), filter.minEnginePowerHp(), filter.maxEnginePowerHp())) {
      return false;
    }
    if (!matchesRange(
        model.getEngineVolumeLiters(),
        filter.minEngineVolumeLiters(),
        filter.maxEngineVolumeLiters())) {
      return false;
    }
    if (filter.gearboxType() != null && model.getGearboxType() != filter.gearboxType()) {
      return false;
    }
    if (filter.driveType() != null && model.getDriveType() != filter.driveType()) {
      return false;
    }
    if (filter.color() != null && !car.getColor().equalsIgnoreCase(filter.color())) {
      return false;
    }
    return true;
  }

  private static boolean matchesRange(BigDecimal value, BigDecimal min, BigDecimal max) {
    if (min != null && value.compareTo(min) < 0) {
      return false;
    }
    if (max != null && value.compareTo(max) > 0) {
      return false;
    }
    return true;
  }

  private static boolean matchesRange(int value, Integer min, Integer max) {
    if (min != null && value < min) {
      return false;
    }
    if (max != null && value > max) {
      return false;
    }
    return true;
  }
}
