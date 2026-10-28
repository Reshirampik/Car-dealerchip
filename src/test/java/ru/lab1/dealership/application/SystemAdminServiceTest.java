package ru.lab1.dealership.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
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

class SystemAdminServiceTest {
  @Test
  void shouldCrudUsersAndGetMissingEntity() {
    SystemAdminService service = service();
    User user = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    service.createUser(user);

    assertEquals("Ivan Client", service.getUser(user.getId()).getFullName());
    service.updateUser(user.getId(), "Ivan Updated", UserRole.CLIENT);
    assertEquals("Ivan Updated", service.getUser(user.getId()).getFullName());

    service.deleteUser(user.getId());
    assertThrows(EntityNotFoundException.class, () -> service.getUser(user.getId()));
  }

  @Test
  void shouldStoreOrdersAndRequests() {
    SystemAdminService service = service();
    CarModel model = mercedesModel();
    service.createCarModel(model);
    CarInStock car =
        new CarInStock(UUID.randomUUID(), model, "obsidian black", BigDecimal.valueOf(4690000));
    service.createCarInStock(car);
    SparePart part =
        new SparePart(
            UUID.randomUUID(), "Brake pads", BigDecimal.valueOf(24000), Set.of(model.getId()));
    service.createSparePart(part);

    User client = new User(UUID.randomUUID(), "Ivan Client", UserRole.CLIENT);
    User manager = new User(UUID.randomUUID(), "Olga Manager", UserRole.MANAGER);
    service.createUser(client);
    service.createUser(manager);

    CommonOrder order = new CommonOrder(UUID.randomUUID(), client, manager, car);
    service.saveCommonOrder(order);
    TestDriveRequest request =
        new TestDriveRequest(UUID.randomUUID(), client, car, LocalDateTime.now().plusDays(1));
    service.saveTestDriveRequest(request);

    assertEquals(1, service.listCommonOrders().size());
    assertEquals(1, service.listTestDriveRequests().size());
  }

  private static SystemAdminService service() {
    return new SystemAdminService(
        new CarModelRepository(),
        new CarInStockRepository(),
        new SparePartRepository(),
        new UserRepository(),
        new CommonOrderRepository(),
        new CustomOrderRepository(),
        new TestDriveRequestRepository());
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
        BigDecimal.valueOf(4100000));
  }
}
