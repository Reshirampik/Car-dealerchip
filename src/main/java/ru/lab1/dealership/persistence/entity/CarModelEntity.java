package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.ComponentType;

@Entity
@Table(name = "car_models")
public class CarModelEntity extends BaseEntity {
  @Column(nullable = false)
  private String brand;

  @Column(name = "model_name", nullable = false)
  private String modelName;

  @Enumerated(EnumType.STRING)
  @Column(name = "body_type", nullable = false)
  private BodyType bodyType;

  @Enumerated(EnumType.STRING)
  @Column(name = "fuel_type", nullable = false)
  private FuelType fuelType;

  @Column(name = "engine_power_hp", nullable = false)
  private int enginePowerHp;

  @Column(name = "engine_volume_liters", nullable = false)
  private BigDecimal engineVolumeLiters;

  @Enumerated(EnumType.STRING)
  @Column(name = "gearbox_type", nullable = false)
  private GearboxType gearboxType;

  @Enumerated(EnumType.STRING)
  @Column(name = "drive_type", nullable = false)
  private DriveType driveType;

  @Column(name = "base_price", nullable = false)
  private BigDecimal basePrice;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "car_model_required_components",
      joinColumns = @JoinColumn(name = "car_model_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "component_type", nullable = false)
  private Set<ComponentType> requiredComponentTypes = new LinkedHashSet<>();

  public CarModelEntity() {}

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getModelName() {
    return modelName;
  }

  public void setModelName(String modelName) {
    this.modelName = modelName;
  }

  public BodyType getBodyType() {
    return bodyType;
  }

  public void setBodyType(BodyType bodyType) {
    this.bodyType = bodyType;
  }

  public FuelType getFuelType() {
    return fuelType;
  }

  public void setFuelType(FuelType fuelType) {
    this.fuelType = fuelType;
  }

  public int getEnginePowerHp() {
    return enginePowerHp;
  }

  public void setEnginePowerHp(int enginePowerHp) {
    this.enginePowerHp = enginePowerHp;
  }

  public BigDecimal getEngineVolumeLiters() {
    return engineVolumeLiters;
  }

  public void setEngineVolumeLiters(BigDecimal engineVolumeLiters) {
    this.engineVolumeLiters = engineVolumeLiters;
  }

  public GearboxType getGearboxType() {
    return gearboxType;
  }

  public void setGearboxType(GearboxType gearboxType) {
    this.gearboxType = gearboxType;
  }

  public DriveType getDriveType() {
    return driveType;
  }

  public void setDriveType(DriveType driveType) {
    this.driveType = driveType;
  }

  public BigDecimal getBasePrice() {
    return basePrice;
  }

  public void setBasePrice(BigDecimal basePrice) {
    this.basePrice = basePrice;
  }

  public Set<ComponentType> getRequiredComponentTypes() {
    return requiredComponentTypes;
  }

  public void setRequiredComponentTypes(Set<ComponentType> requiredComponentTypes) {
    this.requiredComponentTypes = requiredComponentTypes;
  }

  public String displayName() {
    return brand + " " + modelName;
  }
}
