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
import ru.lab1.dealership.service.ComponentOptionService;
import ru.lab1.dealership.web.dto.ComponentOptionRequest;
import ru.lab1.dealership.web.dto.ComponentOptionResponse;

@Profile("storage-service")
@RestController
@RequestMapping("/api/component-options")
@PreAuthorize("isAuthenticated()")
public class ComponentOptionController {
  private final ComponentOptionService componentOptionService;

  public ComponentOptionController(ComponentOptionService componentOptionService) {
    this.componentOptionService = componentOptionService;
  }

  @GetMapping
  public List<ComponentOptionResponse> list() {
    return componentOptionService.list();
  }

  @GetMapping("/{id}")
  public ComponentOptionResponse get(@PathVariable UUID id) {
    return componentOptionService.get(id);
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public ComponentOptionResponse create(@RequestBody ComponentOptionRequest request) {
    return componentOptionService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public ComponentOptionResponse update(
      @PathVariable UUID id, @RequestBody ComponentOptionRequest request) {
    return componentOptionService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    componentOptionService.delete(id);
  }
}
