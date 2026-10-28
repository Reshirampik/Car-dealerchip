package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.config.ComponentOption;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.part.SparePart;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.SparePartRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class ApplicationBranchCoverageTest {
  @Test
  void shouldCancelCommonOrder() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    OrderService orderService =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    CommonOrder order = orderService.createOrder(client.getId(), car.getId());
    orderService.cancel(order.getId());

    assertEquals(
        ru.lab1.dealership.domain.order.CommonOrderStatus.CANCELLED,
        orderService.getOrder(order.getId()).getStatus());
  }

  @Test
  void shouldRejectCancellingCompletedCommonOrder() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    OrderService orderService =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    CommonOrder order = orderService.createOrder(client.getId(), car.getId());
    orderService.approveByManager(order.getId());
    orderService.moveToWaitingPayment(order.getId());
    orderService.markPaid(order.getId());
    orderService.markReadyForPickup(order.getId());
    orderService.complete(order.getId());

    assertThrows(DomainValidationException.class, () -> orderService.cancel(order.getId()));
  }

  @Test
  void shouldCancelCustomOrder() {
    UserRepository userRepository = new UserRepository();
    CarModelRepository carModelRepository = new CarModelRepository();
    CustomOrderRepository customOrderRepository = new CustomOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    CustomOrderService customOrderService =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    carModelRepository.save(model);

    CarConfiguration configuration = buildConfiguration(model);
    CustomOrder order = customOrderService.createOrder(client.getId(), configuration);
    customOrderService.cancel(order.getId());

    assertEquals(
        ru.lab1.dealership.domain.order.CustomOrderStatus.CANCELLED,
        customOrderService.getOrder(order.getId()).getStatus());
  }

  @Test
  void shouldRejectCancellingCompletedCustomOrder() {
    UserRepository userRepository = new UserRepository();
    CarModelRepository carModelRepository = new CarModelRepository();
    CustomOrderRepository customOrderRepository = new CustomOrderRepository();
    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    CustomOrderService customOrderService =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model = mercedesModel();
    carModelRepository.save(model);

    CarConfiguration configuration = buildConfiguration(model);
    CustomOrder order = customOrderService.createOrder(client.getId(), configuration);
    customOrderService.approveByWarehouse(order.getId());
    customOrderService.moveToWaitingPayment(order.getId());
    customOrderService.markPaid(order.getId());
    customOrderService.moveToWaitingDelivery(order.getId());
    customOrderService.markReadyForPickup(order.getId());
    customOrderService.complete(order.getId());

    assertThrows(DomainValidationException.class, () -> customOrderService.cancel(order.getId()));
  }

  @Test
  void shouldRemoveCarFromTestDriveList() {
    UserRepository userRepository = new UserRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    TestDriveRequestRepository testDriveRequestRepository = new TestDriveRequestRepository();
    TestDriveService testDriveService =
        new TestDriveService(testDriveRequestRepository, userRepository, carInStockRepository);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    userRepository.save(client);

    CarModel model = mercedesModel();
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    testDriveService.addCarForTestDrive(car.getId());
    assertTrue(testDriveService.listCarsForTestDrive().size() == 1);

    testDriveService.removeCarFromTestDrive(car.getId());
    assertTrue(testDriveService.listCarsForTestDrive().isEmpty());

    assertThrows(
        DomainValidationException.class,
        () ->
            testDriveService.createRequest(
                client.getId(), car.getId(), LocalDateTime.now().plusDays(1)));
  }

  @Test
  void shouldDeleteWarehouseEntities() {
    WarehouseService warehouseService =
        new WarehouseService(
            new CarModelRepository(), new CarInStockRepository(), new SparePartRepository());

    CarModel model = mercedesModel();
    warehouseService.createCarModel(model);
    CarInStock car = new CarInStock(UUID.randomUUID(), model, "black", new BigDecimal("4690000"));
    warehouseService.createCarInStock(car);
    SparePart part =
        new SparePart(
            UUID.randomUUID(), "Brake pads", new BigDecimal("24000"), Set.of(model.getId()));
    warehouseService.createSparePart(part);

    warehouseService.deleteCarModel(model.getId());
    warehouseService.deleteCarInStock(car.getId());
    warehouseService.deleteSparePart(part.getId());

    assertThrows(EntityNotFoundException.class, () -> warehouseService.getCarModel(model.getId()));
    assertThrows(EntityNotFoundException.class, () -> warehouseService.getCarInStock(car.getId()));
    assertThrows(EntityNotFoundException.class, () -> warehouseService.getSparePart(part.getId()));
  }

  private static CarConfiguration buildConfiguration(CarModel model) {
    CarConfiguration configuration = new CarConfiguration(model);
    UUID modelId = model.getId();

    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            new BigDecimal("95000"),
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "9G-TRONIC",
            BigDecimal.ZERO,
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "AMG steering wheel",
            new BigDecimal("25000"),
            Set.of(modelId)));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            new BigDecimal("110000"),
            Set.of(modelId)));

    return configuration;
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
        new BigDecimal("4100000"));
  }
}
