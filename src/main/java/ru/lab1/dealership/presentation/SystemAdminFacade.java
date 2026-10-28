package ru.lab1.dealership.presentation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.application.SystemAdminService;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.part.SparePart;
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;

public final class SystemAdminFacade {
  private final SystemAdminService systemAdminService;

  public SystemAdminFacade(SystemAdminService systemAdminService) {
    this.systemAdminService = systemAdminService;
  }

  public User createUser(User user) {
    return systemAdminService.createUser(user);
  }

  public User updateUser(UUID id, String fullName, UserRole role) {
    return systemAdminService.updateUser(id, fullName, role);
  }

  public List<User> listUsers() {
    return systemAdminService.listUsers();
  }

  public CarModel createCarModel(CarModel model) {
    return systemAdminService.createCarModel(model);
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
    return systemAdminService.updateCarModel(
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
    return systemAdminService.listCarModels();
  }

  public CarInStock createCarInStock(CarInStock car) {
    return systemAdminService.createCarInStock(car);
  }

  public CarInStock updateCarInStock(UUID id, String color, BigDecimal price) {
    return systemAdminService.updateCarInStock(id, color, price);
  }

  public List<CarInStock> listCarInStock() {
    return systemAdminService.listCarInStock();
  }

  public SparePart createSparePart(SparePart part) {
    return systemAdminService.createSparePart(part);
  }

  public SparePart updateSparePart(
      UUID id, String name, BigDecimal price, Set<UUID> compatibleIds) {
    return systemAdminService.updateSparePart(id, name, price, compatibleIds);
  }

  public List<SparePart> listSpareParts() {
    return systemAdminService.listSpareParts();
  }

  public CommonOrder saveCommonOrder(CommonOrder order) {
    return systemAdminService.saveCommonOrder(order);
  }

  public List<CommonOrder> listCommonOrders() {
    return systemAdminService.listCommonOrders();
  }

  public CustomOrder saveCustomOrder(CustomOrder order) {
    return systemAdminService.saveCustomOrder(order);
  }

  public List<CustomOrder> listCustomOrders() {
    return systemAdminService.listCustomOrders();
  }

  public TestDriveRequest saveTestDriveRequest(TestDriveRequest request) {
    return systemAdminService.saveTestDriveRequest(request);
  }

  public List<TestDriveRequest> listTestDriveRequests() {
    return systemAdminService.listTestDriveRequests();
  }
}
