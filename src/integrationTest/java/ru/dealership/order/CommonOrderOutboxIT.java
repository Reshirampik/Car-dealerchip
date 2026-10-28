package ru.lab1.dealership.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import ru.lab1.dealership.DealershipApplication;
import ru.lab1.dealership.domain.car.BodyType;
import ru.lab1.dealership.domain.car.DriveType;
import ru.lab1.dealership.domain.car.FuelType;
import ru.lab1.dealership.domain.car.GearboxType;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.order.CommonOrderStatus;
import ru.lab1.dealership.domain.order.OutboxEventStatus;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.OutboxEventRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.service.CommonOrderService;
import ru.lab1.dealership.service.OutboxPublisherService;
import ru.lab1.dealership.web.dto.CreateCommonOrderRequest;
import ru.lab1.dealership.web.dto.OrderResponse;

@SpringBootTest(
    classes = DealershipApplication.class,
    properties = {
      "spring.rabbitmq.listener.simple.auto-startup=false",
      "spring.rabbitmq.listener.direct.auto-startup=false"
    })
@ActiveProfiles({"order-service", "test"})
class CommonOrderOutboxIT {

  @TestConfiguration
  static class JwtConfig {
    @Bean
    JwtDecoder jwtDecoder() {
      return token ->
          Jwt.withTokenValue(token)
              .header("alg", "none")
              .claim("sub", "integration-test")
              .claim("preferred_username", "integration-test")
              .issuedAt(Instant.now())
              .expiresAt(Instant.now().plusSeconds(3600))
              .build();
    }
  }

  @MockBean private RabbitTemplate rabbitTemplate;

  private final UserRepository userRepository;
  private final CarModelRepository carModelRepository;
  private final CarInStockRepository carInStockRepository;
  private final CommonOrderService commonOrderService;
  private final OutboxEventRepository outboxEventRepository;
  private final OutboxPublisherService outboxPublisherService;

  @Autowired
  CommonOrderOutboxIT(
      UserRepository userRepository,
      CarModelRepository carModelRepository,
      CarInStockRepository carInStockRepository,
      CommonOrderService commonOrderService,
      OutboxEventRepository outboxEventRepository,
      OutboxPublisherService outboxPublisherService) {
    this.userRepository = userRepository;
    this.carModelRepository = carModelRepository;
    this.carInStockRepository = carInStockRepository;
    this.commonOrderService = commonOrderService;
    this.outboxEventRepository = outboxEventRepository;
    this.outboxPublisherService = outboxPublisherService;
  }

  @Test
  void shouldCreateOrderCreateOutboxAndPublishToRabbit() {
    UserEntity client = new UserEntity("Client User", UserRole.CLIENT);
    client = userRepository.save(client);

    UserEntity manager = new UserEntity("Manager User", UserRole.MANAGER);
    manager = userRepository.save(manager);

    CarModelEntity model = new CarModelEntity();
    model.setBrand("BMW");
    model.setModelName("320i");
    model.setBodyType(BodyType.SEDAN);
    model.setFuelType(FuelType.GASOLINE);
    model.setEnginePowerHp(184);
    model.setEngineVolumeLiters(new BigDecimal("2.0"));
    model.setGearboxType(GearboxType.AUTOMATIC);
    model.setDriveType(DriveType.RWD);
    model.setBasePrice(new BigDecimal("5000000"));
    model.setRequiredComponentTypes(Set.of(ComponentType.WHEELS, ComponentType.TRANSMISSION));
    model = carModelRepository.save(model);

    CarInStockEntity car = new CarInStockEntity();
    car.setModel(model);
    car.setColor("black");
    car.setPrice(new BigDecimal("5290000"));
    car.setAvailableForTestDrive(true);
    car = carInStockRepository.save(car);

    OrderResponse created =
        commonOrderService.create(new CreateCommonOrderRequest(client.getId(), car.getId()));
    assertEquals(CommonOrderStatus.CREATED, created.status());
    assertEquals(manager.getId(), created.managerId());

    OrderResponse approved = commonOrderService.approve(created.id());
    assertEquals(CommonOrderStatus.APPROVED_BY_MANAGER, approved.status());

    OrderResponse waitingPayment = commonOrderService.waitPayment(created.id());
    assertEquals(CommonOrderStatus.WAITING_PAYMENT, waitingPayment.status());

    OrderResponse paid = commonOrderService.markPaid(created.id());
    assertEquals(CommonOrderStatus.PAID, paid.status());

    assertEquals(
        1, outboxEventRepository.findAllByRemovedFalseAndStatus(OutboxEventStatus.NEW).size());

    outboxPublisherService.publishPendingEvents();

    var sent = outboxEventRepository.findAllByRemovedFalseAndStatus(OutboxEventStatus.SENT);
    assertEquals(1, sent.size());
    assertNotNull(sent.get(0).getPublishedAt());
  }
}
