package ru.lab1.dealership.web.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.AvailableCarGrpcClient;
import ru.lab1.dealership.web.dto.CarInStockResponse;

@Profile("order-service")
@RestController
@RequestMapping("/api/v1/cars")
@PreAuthorize("hasAnyRole('USER','MANAGER','ADMIN')")
public class AvailableCarController {
  private final AvailableCarGrpcClient availableCarGrpcClient;

  public AvailableCarController(AvailableCarGrpcClient availableCarGrpcClient) {
    this.availableCarGrpcClient = availableCarGrpcClient;
  }

  @GetMapping
  public List<CarInStockResponse> list() {
    return availableCarGrpcClient.listAvailableCars();
  }

  @GetMapping("/{id}")
  public CarInStockResponse get(@PathVariable UUID id) {
    return availableCarGrpcClient.getAvailableCar(id);
  }
}
