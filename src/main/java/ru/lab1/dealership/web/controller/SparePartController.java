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
import ru.lab1.dealership.service.SparePartService;
import ru.lab1.dealership.web.dto.SparePartRequest;
import ru.lab1.dealership.web.dto.SparePartResponse;

@Profile("storage-service")
@RestController
@RequestMapping("/api/spare-parts")
@PreAuthorize("isAuthenticated()")
public class SparePartController {
  private final SparePartService sparePartService;

  public SparePartController(SparePartService sparePartService) {
    this.sparePartService = sparePartService;
  }

  @GetMapping
  public List<SparePartResponse> list() {
    return sparePartService.list();
  }

  @PostMapping
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.CREATED)
  public SparePartResponse create(@RequestBody SparePartRequest request) {
    return sparePartService.create(request);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  public SparePartResponse update(@PathVariable UUID id, @RequestBody SparePartRequest request) {
    return sparePartService.update(id, request);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAnyRole('WAREHOUSE_ADMIN','ADMIN')")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable UUID id) {
    sparePartService.delete(id);
  }
}
