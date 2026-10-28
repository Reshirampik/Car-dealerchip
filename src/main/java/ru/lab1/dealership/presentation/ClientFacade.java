package ru.lab1.dealership.presentation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.application.CarCatalogService;
import ru.lab1.dealership.application.CarFilter;
import ru.lab1.dealership.application.CustomOrderService;
import ru.lab1.dealership.application.OrderService;
import ru.lab1.dealership.application.TestDriveService;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;

public final class ClientFacade {
  private final CarCatalogService carCatalogService;
  private final OrderService orderService;
  private final CustomOrderService customOrderService;
  private final TestDriveService testDriveService;

  public ClientFacade(
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

  public CarInStock viewCar(UUID carId) {
    return carCatalogService.getCar(carId);
  }

  public CommonOrder orderCarFromStock(UUID clientId, UUID carId) {
    return orderService.createOrder(clientId, carId);
  }

  public CustomOrder orderCarWithConfiguration(UUID clientId, CarConfiguration configuration) {
    return customOrderService.createOrder(clientId, configuration);
  }

  public TestDriveRequest requestTestDrive(UUID clientId, UUID carId, LocalDateTime startDateTime) {
    return testDriveService.createRequest(clientId, carId, startDateTime);
  }
}
