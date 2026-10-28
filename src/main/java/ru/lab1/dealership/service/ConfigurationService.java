package ru.lab1.dealership.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.IncompatibleComponentException;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.ComponentOptionEntity;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.ComponentOptionRepository;
import ru.lab1.dealership.web.dto.ComponentSelectionRequest;
import ru.lab1.dealership.web.dto.ConfigurationRequest;
import ru.lab1.dealership.web.dto.ConfigurationResponse;
import ru.lab1.dealership.web.dto.ConfigurationSelectionResponse;

@Profile("storage-service")
@Service
@Transactional
public class ConfigurationService {
  private final CarModelRepository carModelRepository;
  private final ComponentOptionRepository componentOptionRepository;

  public ConfigurationService(
      CarModelRepository carModelRepository, ComponentOptionRepository componentOptionRepository) {
    this.carModelRepository = carModelRepository;
    this.componentOptionRepository = componentOptionRepository;
  }

  @Transactional(readOnly = true)
  public ConfigurationResponse calculate(ConfigurationRequest request) {
    validateRequest(request);
    CarModelEntity model =
        carModelRepository
            .findByIdAndRemovedFalse(request.modelId())
            .orElseThrow(EntityLookupSupport.notFound("Car model", request.modelId()));

    Map<ComponentType, ComponentSelectionRequest> selections = new LinkedHashMap<>();
    for (ComponentSelectionRequest selection : request.selections()) {
      selections.put(selection.componentType(), selection);
    }

    Set<ComponentType> required = model.getRequiredComponentTypes();
    for (ComponentType componentType : required) {
      if (!selections.containsKey(componentType)) {
        throw new DomainValidationException("Missing required component: " + componentType);
      }
    }

    List<ConfigurationSelectionResponse> responseSelections = new ArrayList<>();
    BigDecimal total = model.getBasePrice();
    for (ComponentSelectionRequest selection : request.selections()) {
      ComponentOptionEntity option =
          componentOptionRepository
              .findByIdAndRemovedFalse(selection.optionId())
              .orElseThrow(EntityLookupSupport.notFound("Component option", selection.optionId()));
      if (option.getComponentType() != selection.componentType()) {
        throw new DomainValidationException("Option type does not match selected component.");
      }
      if (!option.getCompatibleModels().contains(model)) {
        throw new IncompatibleComponentException(
            "Selected component is not available for model " + model.displayName() + ".");
      }
      total = total.add(option.getDeltaPrice());
      responseSelections.add(
          new ConfigurationSelectionResponse(
              option.getComponentType(),
              option.getId(),
              option.getOptionName(),
              option.getDeltaPrice()));
    }

    return new ConfigurationResponse(
        model.getId(), model.displayName(), model.getBasePrice(), total, responseSelections);
  }

  private void validateRequest(ConfigurationRequest request) {
    if (request == null
        || request.modelId() == null
        || request.selections() == null
        || request.selections().isEmpty()) {
      throw new DomainValidationException("Invalid configuration request.");
    }
  }
}
