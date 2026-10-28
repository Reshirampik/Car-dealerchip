package ru.lab1.dealership.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import ru.lab1.dealership.DealershipApplication;
import ru.lab1.dealership.persistence.repository.AssemblyOrderRepository;
import ru.lab1.dealership.service.AssemblyOrderService;
import ru.lab1.dealership.web.dto.AssemblyOrderRequest;
import ru.lab1.dealership.web.dto.AssemblyOrderResponse;

@SpringBootTest(classes = DealershipApplication.class)
@ActiveProfiles({"storage-service", "test"})
class AssemblyOrderServiceIT {

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

  private final AssemblyOrderService assemblyOrderService;
  private final AssemblyOrderRepository assemblyOrderRepository;

  @Autowired
  AssemblyOrderServiceIT(
      AssemblyOrderService assemblyOrderService, AssemblyOrderRepository assemblyOrderRepository) {
    this.assemblyOrderService = assemblyOrderService;
    this.assemblyOrderRepository = assemblyOrderRepository;
  }

  @Test
  void shouldCreateReadUpdateAssembleAndDeleteAssemblyOrder() {
    UUID sourceOrderId = UUID.randomUUID();
    UUID carId = UUID.randomUUID();
    UUID modelId = UUID.randomUUID();
    UUID warehouseEmployeeId = UUID.randomUUID();
    UUID component1 = UUID.randomUUID();
    UUID component2 = UUID.randomUUID();

    AssemblyOrderRequest createRequest =
        new AssemblyOrderRequest(
            sourceOrderId,
            "COMMON",
            carId,
            modelId,
            Set.of(component1, component2),
            warehouseEmployeeId,
            "CREATED");

    AssemblyOrderResponse created = assemblyOrderService.create(createRequest);
    assertTrue(created.id() != null);
    assertEquals("CREATED", created.status());

    AssemblyOrderResponse read = assemblyOrderService.get(created.id());
    assertEquals(created.id(), read.id());

    AssemblyOrderRequest updateRequest =
        new AssemblyOrderRequest(
            sourceOrderId,
            "COMMON",
            carId,
            modelId,
            Set.of(component1),
            warehouseEmployeeId,
            "CREATED");

    AssemblyOrderResponse updated = assemblyOrderService.update(created.id(), updateRequest);
    assertEquals(1, updated.requiredComponentIds().size());

    AssemblyOrderResponse assembled = assemblyOrderService.assemble(created.id());
    assertEquals("ASSEMBLED", assembled.status());

    assemblyOrderService.delete(created.id());
    assertTrue(assemblyOrderRepository.findByIdAndRemovedFalse(created.id()).isEmpty());
  }
}
