package ru.lab1.dealership.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.ComponentOptionEntity;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.ComponentOptionRepository;
import ru.lab1.dealership.web.dto.ComponentOptionRequest;
import ru.lab1.dealership.web.dto.ComponentOptionResponse;

@Profile("storage-service")
@Service
@Transactional
public class ComponentOptionService {
  private final ComponentOptionRepository componentOptionRepository;
  private final CarModelRepository carModelRepository;
  private final ModelMapper modelMapper;

  public ComponentOptionService(
      ComponentOptionRepository componentOptionRepository,
      CarModelRepository carModelRepository,
      ModelMapper modelMapper) {
    this.componentOptionRepository = componentOptionRepository;
    this.carModelRepository = carModelRepository;
    this.modelMapper = modelMapper;
  }

  @Transactional(readOnly = true)
  public List<ComponentOptionResponse> list() {
    return componentOptionRepository.findAllByRemovedFalse().stream()
        .map(this::toResponse)
        .toList();
  }

  @Transactional(readOnly = true)
  public ComponentOptionResponse get(UUID id) {
    return toResponse(
        componentOptionRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Component option", id)));
  }

  public ComponentOptionResponse create(ComponentOptionRequest request) {
    ComponentOptionEntity entity = toEntity(request);
    return toResponse(componentOptionRepository.save(entity));
  }

  public ComponentOptionResponse update(UUID id, ComponentOptionRequest request) {
    ComponentOptionEntity entity =
        componentOptionRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Component option", id));
    entity.setComponentType(request.componentType());
    entity.setOptionName(request.optionName());
    entity.setDeltaPrice(request.deltaPrice());
    entity.setCompatibleModels(resolveModels(request.compatibleModelIds()));
    return toResponse(componentOptionRepository.save(entity));
  }

  public void delete(UUID id) {
    ComponentOptionEntity entity =
        componentOptionRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Component option", id));
    entity.setRemoved(true);
    componentOptionRepository.save(entity);
  }

  private ComponentOptionEntity toEntity(ComponentOptionRequest request) {
    if (request == null
        || request.componentType() == null
        || request.optionName() == null
        || request.optionName().isBlank()
        || request.deltaPrice() == null
        || request.compatibleModelIds() == null
        || request.compatibleModelIds().isEmpty()) {
      throw new DomainValidationException("Invalid component option request.");
    }
    ComponentOptionEntity entity = modelMapper.map(request, ComponentOptionEntity.class);
    entity.setCompatibleModels(resolveModels(request.compatibleModelIds()));
    return entity;
  }

  private Set<CarModelEntity> resolveModels(Set<UUID> ids) {
    Set<CarModelEntity> result = new LinkedHashSet<>();
    for (UUID id : ids) {
      result.add(
          carModelRepository
              .findByIdAndRemovedFalse(id)
              .orElseThrow(EntityLookupSupport.notFound("Car model", id)));
    }
    return result;
  }

  private ComponentOptionResponse toResponse(ComponentOptionEntity entity) {
    Set<UUID> compatibleModelIds =
        entity.getCompatibleModels().stream()
            .map(CarModelEntity::getId)
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    return new ComponentOptionResponse(
        entity.getId(),
        entity.getComponentType(),
        entity.getOptionName(),
        entity.getDeltaPrice(),
        compatibleModelIds,
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
