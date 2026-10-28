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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.TestDriveService;
import ru.lab1.dealership.web.dto.TestDriveRequestDto;
import ru.lab1.dealership.web.dto.TestDriveResponse;

@Profile("order-service")
@RestController
@RequestMapping("/api/test-drives")
@PreAuthorize("isAuthenticated()")
public class TestDriveController {
  private final TestDriveService testDriveService;

  public TestDriveController(TestDriveService testDriveService) {
    this.testDriveService = testDriveService;
  }

  @GetMapping
  public List<TestDriveResponse> list() {
    return testDriveService.list();
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('USER','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public TestDriveResponse create(@RequestBody TestDriveRequestDto request) {
    return testDriveService.create(request);
  }

  @PostMapping("/cars/{carId}/allow")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public void allow(@PathVariable UUID carId) {
    testDriveService.allowCar(carId);
  }

  @PostMapping("/cars/{carId}/disallow")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public void disallow(@PathVariable UUID carId) {
    testDriveService.disallowCar(carId);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    // soft delete endpoint can be added later
  }
}
