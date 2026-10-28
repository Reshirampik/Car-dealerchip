package ru.dealership;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.TestDriveRequestEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.TestDriveRequestRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.service.CurrentUserService;
import ru.lab1.dealership.service.TestDriveService;

@ExtendWith(MockitoExtension.class)
class TestDriveServiceListTest {

  @Mock private TestDriveRequestRepository testDriveRequestRepository;
  @Mock private UserRepository userRepository;
  @Mock private CarInStockRepository carInStockRepository;
  @Mock private CurrentUserService currentUserService;

  @InjectMocks private TestDriveService service;

  @Test
  void shouldListRequests() {
    when(currentUserService.hasAuthentication()).thenReturn(false);

    UUID clientId = UUID.randomUUID();
    UUID carId = UUID.randomUUID();
    UUID request1Id = UUID.randomUUID();
    UUID request2Id = UUID.randomUUID();

    UserEntity client = new UserEntity("Client", UserRole.CLIENT);
    client.setId(clientId);

    CarModelEntity model = new CarModelEntity();
    model.setId(UUID.randomUUID());
    model.setBrand("Mercedes-Benz");
    model.setModelName("C 200");
    model.setBodyType(BodyType.SEDAN);
    model.setFuelType(FuelType.GASOLINE);
    model.setEnginePowerHp(204);
    model.setEngineVolumeLiters(new BigDecimal("1.5"));
    model.setGearboxType(GearboxType.AUTOMATIC);
    model.setDriveType(DriveType.RWD);
    model.setBasePrice(new BigDecimal("4500000"));
    model.setRequiredComponentTypes(Set.of(ComponentType.WHEELS, ComponentType.TRANSMISSION));

    CarInStockEntity car = new CarInStockEntity();
    car.setId(carId);
    car.setModel(model);
    car.setColor("black");
    car.setPrice(new BigDecimal("4690000"));
    car.setAvailableForTestDrive(true);

    TestDriveRequestEntity first = new TestDriveRequestEntity();
    first.setId(request1Id);
    first.setClient(client);
    first.setCar(car);
    first.setStartDateTime(Instant.now().plusSeconds(3600));

    TestDriveRequestEntity second = new TestDriveRequestEntity();
    second.setId(request2Id);
    second.setClient(client);
    second.setCar(car);
    second.setStartDateTime(Instant.now().plusSeconds(7200));

    when(testDriveRequestRepository.findAllByRemovedFalse()).thenReturn(List.of(first, second));

    assertEquals(2, service.list().size());
  }
}
