package ru.lab1.dealership.presentation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.application.CarCatalogService;
import ru.lab1.dealership.application.CustomOrderService;
import ru.lab1.dealership.application.ManagerAssignmentService;
import ru.lab1.dealership.application.OrderService;
import ru.lab1.dealership.application.SystemAdminService;
import ru.lab1.dealership.application.TestDriveService;
import ru.lab1.dealership.application.WarehouseService;
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
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.SparePartRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

class PresentationDelegationTest {
  @Test
  void shouldCoverAllFacadeMethods() {
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
    SystemAdminService systemAdminService =
        new SystemAdminService(
            carModelRepository,
            carInStockRepository,
            sparePartRepository,
            userRepository,
            commonOrderRepository,
            customOrderRepository,
            testDriveRequestRepository);

    ClientFacade clientFacade =
        new ClientFacade(carCatalogService, orderService, customOrderService, testDriveService);
    ManagerFacade managerFacade =
        new ManagerFacade(carCatalogService, orderService, customOrderService, testDriveService);
    WarehouseAdminFacade warehouseFacade =
        new WarehouseAdminFacade(warehouseService, carCatalogService);
    SystemAdminFacade adminFacade = new SystemAdminFacade(systemAdminService);

    User client = new User(UUID.randomUUID(), "Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Manager", UserRole.MANAGER);
    User warehouseAdmin = new User(UUID.randomUUID(), "Warehouse", UserRole.WAREHOUSE_ADMIN);
    User systemAdmin = new User(UUID.randomUUID(), "Admin", UserRole.SYSTEM_ADMIN);

    assertDoesNotThrow(() -> adminFacade.createUser(client));
    assertDoesNotThrow(() -> adminFacade.createUser(manager));
    assertDoesNotThrow(() -> adminFacade.createUser(warehouseAdmin));
    assertDoesNotThrow(() -> adminFacade.createUser(systemAdmin));

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

    assertDoesNotThrow(() -> warehouseFacade.createCarModel(model));
    assertDoesNotThrow(() -> adminFacade.createCarModel(model));

    CarInStock car =
        new CarInStock(UUID.randomUUID(), model, "obsidian black", new BigDecimal("4690000"));

    assertDoesNotThrow(() -> warehouseFacade.createCarInStock(car));
    assertDoesNotThrow(() -> adminFacade.createCarInStock(car));

    SparePart part =
        new SparePart(
            UUID.randomUUID(), "Brake pads", new BigDecimal("24000"), Set.of(model.getId()));

    assertDoesNotThrow(() -> warehouseFacade.createSparePart(part));
    assertDoesNotThrow(() -> adminFacade.createSparePart(part));

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

    assertDoesNotThrow(() -> managerFacade.viewCars(null));
    assertDoesNotThrow(() -> clientFacade.viewCars(null));
    assertDoesNotThrow(() -> clientFacade.viewCar(car.getId()));
    assertDoesNotThrow(() -> managerFacade.listCarsForTestDrive());

    assertDoesNotThrow(() -> managerFacade.addCarForTestDrive(car.getId()));
    assertDoesNotThrow(
        () ->
            clientFacade.requestTestDrive(
                client.getId(), car.getId(), LocalDateTime.now().plusDays(1)));

    CommonOrder commonOrder = orderService.createOrder(client.getId(), car.getId());

    assertDoesNotThrow(() -> managerFacade.listCommonOrders());
    assertDoesNotThrow(() -> managerFacade.getCommonOrder(commonOrder.getId()));
    assertDoesNotThrow(() -> adminFacade.saveCommonOrder(commonOrder));
    assertDoesNotThrow(() -> adminFacade.listCommonOrders());

    assertDoesNotThrow(() -> clientFacade.orderCarFromStock(client.getId(), car.getId()));
    assertDoesNotThrow(() -> clientFacade.orderCarWithConfiguration(client.getId(), configuration));
    assertDoesNotThrow(() -> managerFacade.listCustomOrders());
    assertDoesNotThrow(() -> adminFacade.listCustomOrders());

    TestDriveRequest request =
        testDriveService.createRequest(
            client.getId(), car.getId(), LocalDateTime.now().plusDays(1));
    assertDoesNotThrow(() -> adminFacade.saveTestDriveRequest(request));
    assertDoesNotThrow(() -> adminFacade.listTestDriveRequests());

    assertDoesNotThrow(
        () -> adminFacade.updateUser(client.getId(), "Client Updated", UserRole.CLIENT));
    assertDoesNotThrow(
        () ->
            adminFacade.updateCarModel(
                model.getId(),
                "Mercedes-Benz",
                "C 200 facelift",
                BodyType.SEDAN,
                FuelType.GASOLINE,
                204,
                new BigDecimal("1.5"),
                GearboxType.AUTOMATIC,
                DriveType.RWD,
                new BigDecimal("4150000")));
    assertDoesNotThrow(
        () -> adminFacade.updateCarInStock(car.getId(), "polar white", new BigDecimal("4720000")));
    assertDoesNotThrow(
        () ->
            adminFacade.updateSparePart(
                part.getId(), "Brake pads PRO", new BigDecimal("26000"), Set.of(model.getId())));

    assertDoesNotThrow(() -> adminFacade.listUsers());
    assertDoesNotThrow(() -> adminFacade.listCarModels());
    assertDoesNotThrow(() -> adminFacade.listCarInStock());
    assertDoesNotThrow(() -> adminFacade.listSpareParts());
  }
}
