package ru.lab1.dealership.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import ru.lab1.dealership.application.CarCatalogService;
import ru.lab1.dealership.application.CarFilter;
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
import ru.lab1.dealership.presentation.ClientFacade;
import ru.lab1.dealership.presentation.ManagerFacade;
import ru.lab1.dealership.presentation.SystemAdminFacade;
import ru.lab1.dealership.presentation.WarehouseAdminFacade;

public final class Application {
  public static void main(String[] args) {
    var userRepository = new UserRepository();
    var carModelRepository = new CarModelRepository();
    var carInStockRepository = new CarInStockRepository();
    var commonOrderRepository = new CommonOrderRepository();
    var customOrderRepository = new CustomOrderRepository();
    var testDriveRequestRepository = new TestDriveRequestRepository();
    var sparePartRepository = new SparePartRepository();

    var managerAssignmentService = new ManagerAssignmentService(userRepository, new Random(7));
    var carCatalogService = new CarCatalogService(carInStockRepository);
    var orderService =
        new OrderService(
            commonOrderRepository, userRepository, carInStockRepository, managerAssignmentService);
    var customOrderService =
        new CustomOrderService(
            customOrderRepository, userRepository, carModelRepository, managerAssignmentService);
    var testDriveService =
        new TestDriveService(testDriveRequestRepository, userRepository, carInStockRepository);
    var warehouseService =
        new WarehouseService(carModelRepository, carInStockRepository, sparePartRepository);
    var systemAdminService =
        new SystemAdminService(
            carModelRepository,
            carInStockRepository,
            sparePartRepository,
            userRepository,
            commonOrderRepository,
            customOrderRepository,
            testDriveRequestRepository);

    var clientFacade =
        new ClientFacade(carCatalogService, orderService, customOrderService, testDriveService);
    var managerFacade =
        new ManagerFacade(carCatalogService, orderService, customOrderService, testDriveService);
    var warehouseFacade = new WarehouseAdminFacade(warehouseService, carCatalogService);
    var adminFacade = new SystemAdminFacade(systemAdminService);

    var client =
        adminFacade.createUser(new User(UUID.randomUUID(), "Artem Client", UserRole.CLIENT));
    var manager =
        adminFacade.createUser(new User(UUID.randomUUID(), "Olga Manager", UserRole.MANAGER));
    adminFacade.createUser(
        new User(UUID.randomUUID(), "Sergey Warehouse", UserRole.WAREHOUSE_ADMIN));
    adminFacade.createUser(new User(UUID.randomUUID(), "Admin Root", UserRole.SYSTEM_ADMIN));

    var cClassId = UUID.randomUUID();
    var eClassId = UUID.randomUUID();
    var cClass =
        new CarModel(
            cClassId,
            "Mercedes-Benz",
            "C 200",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            204,
            new BigDecimal("1.5"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("4500000"));
    var eClass =
        new CarModel(
            eClassId,
            "Mercedes-Benz",
            "E 300",
            BodyType.SEDAN,
            FuelType.GASOLINE,
            258,
            new BigDecimal("2.0"),
            GearboxType.AUTOMATIC,
            DriveType.RWD,
            new BigDecimal("6900000"));

    warehouseFacade.createCarModel(cClass);
    warehouseFacade.createCarModel(eClass);

    var c200 =
        new CarInStock(UUID.randomUUID(), cClass, "obsidian black", new BigDecimal("4690000"));
    var e300 =
        new CarInStock(UUID.randomUUID(), eClass, "opalite white", new BigDecimal("7350000"));
    warehouseFacade.createCarInStock(c200);
    warehouseFacade.createCarInStock(e300);

    var brakePads =
        new SparePart(
            UUID.randomUUID(),
            "Mercedes Original brake pads",
            new BigDecimal("24000"),
            Set.of(cClassId, eClassId));
    warehouseFacade.createSparePart(brakePads);

    var config = new CarConfiguration(cClass);
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.WHEELS,
            "19'' AMG",
            new BigDecimal("95000"),
            Set.of(cClassId, eClassId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.TRANSMISSION,
            "9G-TRONIC",
            BigDecimal.ZERO,
            Set.of(cClassId, eClassId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.STEERING_WHEEL,
            "AMG heated steering wheel",
            new BigDecimal("25000"),
            Set.of(cClassId, eClassId)));
    config.selectOption(
        new ComponentOption(
            UUID.randomUUID(),
            ComponentType.INTERIOR,
            "Nappa leather",
            new BigDecimal("110000"),
            Set.of(cClassId)));

    testDriveService.addCarForTestDrive(c200.getId());
    var stockOrder = clientFacade.orderCarFromStock(client.getId(), c200.getId());
    var customOrder = clientFacade.orderCarWithConfiguration(client.getId(), config);
    var testDrive =
        clientFacade.requestTestDrive(
            client.getId(), c200.getId(), LocalDateTime.now().plusDays(1));

    System.out.println(
        "Mercedes-Benz stock cars: " + carCatalogService.listAvailableCars(null).size());
    System.out.println("Stock order status: " + stockOrder.getStatus());
    System.out.println("Custom order total: " + customOrder.getConfiguration().totalPrice());
    System.out.println("Test drive request: " + testDrive.getStartDateTime());
    System.out.println(
        "Managers in system: " + userRepository.findAll().stream().filter(User::isManager).count());
    System.out.println("Assigned manager: " + manager.getFullName());
    System.out.println(
        "Client view filter example: "
            + clientFacade
                .viewCars(
                    new CarFilter(
                        null,
                        null,
                        "Mercedes-Benz",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null))
                .size());
  }
}
