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
import ru.lab1.dealership.service.AssemblyOrderService;
import ru.lab1.dealership.web.dto.AssemblyOrderRequest;
import ru.lab1.dealership.web.dto.AssemblyOrderResponse;

@Profile("storage-service")
@RestController
@RequestMapping("/api/assembly-orders")
@PreAuthorize("isAuthenticated()")
public class AssemblyOrderController {
  private final AssemblyOrderService assemblyOrderService;

  public AssemblyOrderController(AssemblyOrderService assemblyOrderService) {
    this.assemblyOrderService = assemblyOrderService;
  }

  @GetMapping
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public List<AssemblyOrderResponse> list() {
    return assemblyOrderService.list();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public AssemblyOrderResponse get(@PathVariable UUID id) {
    return assemblyOrderService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public AssemblyOrderResponse create(@RequestBody AssemblyOrderRequest request) {
    return assemblyOrderService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public AssemblyOrderResponse update(
      @PathVariable UUID id, @RequestBody AssemblyOrderRequest request) {
    return assemblyOrderService.update(id, request);
  }

  @PostMapping("/{id}/assemble")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public AssemblyOrderResponse assemble(@PathVariable UUID id) {
    return assemblyOrderService.assemble(id);
  }

  @PostMapping("/{id}/fail")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public AssemblyOrderResponse fail(@PathVariable UUID id) {
    return assemblyOrderService.fail(id);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    assemblyOrderService.delete(id);
  }
}
