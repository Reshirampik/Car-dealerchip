package ru.lab1.dealership.web.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.CarInStockService;
import ru.lab1.dealership.web.dto.CarInStockRequest;
import ru.lab1.dealership.web.dto.CarInStockResponse;

@Profile("storage-service")
@RestController
@RequestMapping("/api/cars")
@PreAuthorize("isAuthenticated()")
public class CarInStockController {
  private final CarInStockService carInStockService;

  public CarInStockController(CarInStockService carInStockService) {
    this.carInStockService = carInStockService;
  }

  @GetMapping
  public List<CarInStockResponse> list() {
    return carInStockService.list();
  }

  @GetMapping("/{id}")
  public CarInStockResponse get(@PathVariable UUID id) {
    return carInStockService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public CarInStockResponse create(@RequestBody CarInStockRequest request) {
    return carInStockService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public CarInStockResponse update(@PathVariable UUID id, @RequestBody CarInStockRequest request) {
    return carInStockService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    carInStockService.delete(id);
  }
}
