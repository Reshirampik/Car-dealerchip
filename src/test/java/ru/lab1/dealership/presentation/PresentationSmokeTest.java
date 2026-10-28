package ru.lab1.dealership.presentation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.application.*;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.config.ComponentOption;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.order.CommonOrder;
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

class PresentationSmokeTest {
  @Test
  void shouldCallClientManagerWarehouseAndAdminFacades() {
    UserRepository userRepository = new UserRepository();
    CarModelRepository carModelRepository = new CarModelRepository();
    CarInStockRepository carInStockRepository = new CarInStockRepository();
    CommonOrderRepository commonOrderRepository = new CommonOrderRepository();
    CustomOrderRepository customOrderRepository = new CustomOrderRepository();
    TestDriveRequestRepository testDriveRequestRepository = new TestDriveRequestRepository();
    SparePartRepository sparePartRepository = new SparePartRepository();

    ManagerAssignmentService managerAssignmentService =
        new ManagerAssignmentService(userRepository, new Random(1));
    CarCatalogService carCatalogService = new CarCatalogService(carInStockRepository);
    OrderService orderService =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);
    CustomOrderService customOrderService =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);
    TestDriveService testDriveService =
        new TestDriveService(testDriveRequestRepository, userRepository, carInStockRepository);
    WarehouseService warehouseService =
        new WarehouseService(carModelRepository, carInStockRepository, sparePartRepository);

    ClientFacade clientFacade =
        new ClientFacade(carCatalogService, orderService, customOrderService, testDriveService);
    ManagerFacade managerFacade =
        new ManagerFacade(carCatalogService, orderService, customOrderService, testDriveService);
    WarehouseAdminFacade warehouseFacade =
        new WarehouseAdminFacade(warehouseService, carCatalogService);
    SystemAdminFacade adminFacade =
        new SystemAdminFacade(
            new SystemAdminService(
                carModelRepository,
                carInStockRepository,
                sparePartRepository,
                userRepository,
                commonOrderRepository,
                customOrderRepository,
                testDriveRequestRepository));

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    userRepository.save(client);
    userRepository.save(manager);

    CarModel model =
        new CarModel(
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
    carModelRepository.save(model);

    CarInStock car =
        new CarInStock(UUID.randomUUID(), model, "obsidian black", new BigDecimal("4690000"));
    carInStockRepository.save(car);

    SparePart part =
        new SparePart(
            UUID.randomUUID(), "Brake pads", new BigDecimal("24000"), Set.of(model.getId()));
    sparePartRepository.save(part);

    CarConfiguration configuration = new CarConfiguration(model);
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            new BigDecimal("95000"),
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "9G-TRONIC",
            BigDecimal.ZERO,
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "AMG steering wheel",
            new BigDecimal("25000"),
            Set.of(model.getId())));
    configuration.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            new BigDecimal("110000"),
            Set.of(model.getId())));

    assertDoesNotThrow(() -> managerFacade.addCarForTestDrive(car.getId()));

    assertDoesNotThrow(() -> clientFacade.viewCars(null));
    assertDoesNotThrow(() -> clientFacade.viewCar(car.getId()));
    assertDoesNotThrow(() -> clientFacade.orderCarFromStock(client.getId(), car.getId()));
    assertDoesNotThrow(() -> clientFacade.orderCarWithConfiguration(client.getId(), configuration));
    assertDoesNotThrow(
        () ->
            clientFacade.requestTestDrive(
                client.getId(), car.getId(), LocalDateTime.now().plusDays(1)));

    CommonOrder order = orderService.createOrder(client.getId(), car.getId());

    assertDoesNotThrow(() -> managerFacade.viewCars(null));
    assertDoesNotThrow(() -> managerFacade.listCommonOrders());
    assertDoesNotThrow(() -> managerFacade.getCommonOrder(order.getId()));
    assertDoesNotThrow(() -> managerFacade.addCarForTestDrive(car.getId()));
    assertDoesNotThrow(() -> managerFacade.listCarsForTestDrive());
    assertDoesNotThrow(() -> managerFacade.listTestDriveRequests());

    assertDoesNotThrow(() -> warehouseFacade.listCarModels());
    assertDoesNotThrow(() -> warehouseFacade.listCarInStock());
    assertDoesNotThrow(() -> warehouseFacade.listSpareParts());

    assertDoesNotThrow(() -> adminFacade.listUsers());
    assertDoesNotThrow(() -> adminFacade.listCommonOrders());
    assertDoesNotThrow(() -> adminFacade.listCustomOrders());
    assertDoesNotThrow(() -> adminFacade.listTestDriveRequests());
  }
}
