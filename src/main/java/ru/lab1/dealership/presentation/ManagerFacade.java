package ru.lab1.dealership.presentation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.application.CarCatalogService;
import ru.lab1.dealership.application.CarFilter;
import ru.lab1.dealership.application.CustomOrderService;
import ru.lab1.dealership.application.OrderService;
import ru.lab1.dealership.application.TestDriveService;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;

public final class ManagerFacade {
  private final CarCatalogService carCatalogService;
  private final OrderService orderService;
  private final CustomOrderService customOrderService;
  private final TestDriveService testDriveService;

  public ManagerFacade(
      CarCatalogService carCatalogService,
      OrderService orderService,
      CustomOrderService customOrderService,
      TestDriveService testDriveService) {
    this.carCatalogService = Objects.requireNonNull(carCatalogService, "carCatalogService");
    this.orderService = Objects.requireNonNull(orderService, "orderService");
    this.customOrderService = Objects.requireNonNull(customOrderService, "customOrderService");
    this.testDriveService = Objects.requireNonNull(testDriveService, "testDriveService");
  }

  public List<CarInStock> viewCars(CarFilter filter) {
    return carCatalogService.listAvailableCars(filter);
  }

  public CommonOrder getCommonOrder(UUID orderId) {
    return orderService.getOrder(orderId);
  }

  public List<CommonOrder> listCommonOrders() {
    return orderService.listOrders();
  }

  public CustomOrder getCustomOrder(UUID orderId) {
    return customOrderService.getOrder(orderId);
  }

  public List<CustomOrder> listCustomOrders() {
    return customOrderService.listOrders();
  }

  public CarInStock addCarForTestDrive(UUID carId) {
    return testDriveService.addCarForTestDrive(carId);
  }

  public void removeCarFromTestDrive(UUID carId) {
    testDriveService.removeCarFromTestDrive(carId);
  }

  public List<CarInStock> listCarsForTestDrive() {
    return testDriveService.listCarsForTestDrive();
  }

  public List<TestDriveRequest> listTestDriveRequests() {
    return testDriveService.listRequests();
  }
}
