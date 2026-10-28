package ru.lab1.dealership.domain.car;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;
import ru.lab1.dealership.domain.exception.DomainValidationException;

public final class CarModel implements Identifiable {
  private final UUID id;
  private String brand;
  private String model;
  private BodyType bodyType;
  private FuelType fuelType;
  private int enginePowerHp;
  private BigDecimal engineVolumeLiters;
  private GearboxType gearboxType;
  private DriveType driveType;
  private BigDecimal basePrice;

  public CarModel(
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
    this.id = Objects.requireNonNull(id, "id");
    this.brand = requireNonBlank(brand, "brand");
    this.model = requireNonBlank(model, "model");
    this.bodyType = Objects.requireNonNull(bodyType, "bodyType");
    this.fuelType = Objects.requireNonNull(fuelType, "fuelType");
    this.enginePowerHp = validatePower(enginePowerHp);
    this.engineVolumeLiters = validateVolume(engineVolumeLiters);
    this.gearboxType = Objects.requireNonNull(gearboxType, "gearboxType");
    this.driveType = Objects.requireNonNull(driveType, "driveType");
    this.basePrice = validatePrice(basePrice);
  }

  @Override
  public UUID getId() {
    return id;
  }

  public String getBrand() {
    return brand;
  }

  public String getModel() {
    return model;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public FuelType getFuelType() {
    return fuelType;
  }

  public int getEnginePowerHp() {
    return enginePowerHp;
  }

  public BigDecimal getEngineVolumeLiters() {
    return engineVolumeLiters;
  }

  public GearboxType getGearboxType() {
    return gearboxType;
  }

  public DriveType getDriveType() {
    return driveType;
  }

  public BigDecimal getBasePrice() {
    return basePrice;
  }

  public String displayName() {
    return brand + ' ' + model;
  }

  public void update(
      String brand,
      String model,
      BodyType bodyType,
      FuelType fuelType,
      int enginePowerHp,
      BigDecimal engineVolumeLiters,
      GearboxType gearboxType,
      DriveType driveType,
      BigDecimal basePrice) {
    this.brand = requireNonBlank(brand, "brand");
    this.model = requireNonBlank(model, "model");
    this.bodyType = Objects.requireNonNull(bodyType, "bodyType");
    this.fuelType = Objects.requireNonNull(fuelType, "fuelType");
    this.enginePowerHp = validatePower(enginePowerHp);
    this.engineVolumeLiters = validateVolume(engineVolumeLiters);
    this.gearboxType = Objects.requireNonNull(gearboxType, "gearboxType");
    this.driveType = Objects.requireNonNull(driveType, "driveType");
    this.basePrice = validatePrice(basePrice);
  }

  private static String requireNonBlank(String value, String fieldName) {
    if (value == null || value.isBlank()) {
      throw new DomainValidationException(fieldName + " cannot be blank.");
    }
    return value;
  }

  private static int validatePower(int power) {
    if (power <= 0) {
      throw new DomainValidationException("enginePowerHp must be positive.");
    }
    return power;
  }

  private static BigDecimal validateVolume(BigDecimal volume) {
    Objects.requireNonNull(volume, "engineVolumeLiters");
    if (volume.signum() <= 0) {
      throw new DomainValidationException("engineVolumeLiters must be positive.");
    }
    return volume;
  }

  private static BigDecimal validatePrice(BigDecimal price) {
    Objects.requireNonNull(price, "basePrice");
    if (price.signum() < 0) {
      throw new DomainValidationException("basePrice cannot be negative.");
    }
    return price;
  }
}
