package ru.lab1.dealership.web.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.lab1.dealership.service.ConfigurationService;
import ru.lab1.dealership.web.dto.ConfigurationRequest;
import ru.lab1.dealership.web.dto.ConfigurationResponse;

@Profile("storage-service")
@RestController
@RequestMapping("/api/configurations")
@PreAuthorize("isAuthenticated()")
public class ConfigurationController {
  private final ConfigurationService configurationService;

  public ConfigurationController(ConfigurationService configurationService) {
    this.configurationService = configurationService;
  }

  @PostMapping("/calculate")
  public ConfigurationResponse calculate(@RequestBody ConfigurationRequest request) {
    return configurationService.calculate(request);
  }
}
