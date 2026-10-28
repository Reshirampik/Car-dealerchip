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
import ru.lab1.dealership.persistence.entity.SparePartEntity;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.SparePartRepository;
import ru.lab1.dealership.web.dto.SparePartRequest;
import ru.lab1.dealership.web.dto.SparePartResponse;

@Profile("storage-service")
@Service
@Transactional
public class SparePartService {
  private final SparePartRepository sparePartRepository;
  private final CarModelRepository carModelRepository;
  private final ModelMapper modelMapper;

  public SparePartService(
      SparePartRepository sparePartRepository,
      CarModelRepository carModelRepository,
      ModelMapper modelMapper) {
    this.sparePartRepository = sparePartRepository;
    this.carModelRepository = carModelRepository;
    this.modelMapper = modelMapper;
  }

  @Transactional(readOnly = true)
  public List<SparePartResponse> list() {
    return sparePartRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  public SparePartResponse create(SparePartRequest request) {
    SparePartEntity entity = toEntity(request);
    return toResponse(sparePartRepository.save(entity));
  }

  public SparePartResponse update(UUID id, SparePartRequest request) {
    SparePartEntity entity =
        sparePartRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Spare part", id));
    entity.setPartName(request.partName());
    entity.setPrice(request.price());
    entity.setCompatibleModels(resolveModels(request.compatibleModelIds()));
    return toResponse(sparePartRepository.save(entity));
  }

  public void delete(UUID id) {
    SparePartEntity entity =
        sparePartRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Spare part", id));
    entity.setRemoved(true);
    sparePartRepository.save(entity);
  }

  private SparePartEntity toEntity(SparePartRequest request) {
    if (request == null
        || request.partName() == null
        || request.partName().isBlank()
        || request.price() == null
        || request.compatibleModelIds() == null
        || request.compatibleModelIds().isEmpty()) {
      throw new DomainValidationException("Invalid spare part request.");
    }
    SparePartEntity entity = modelMapper.map(request, SparePartEntity.class);
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

  private SparePartResponse toResponse(SparePartEntity entity) {
    Set<UUID> ids =
        entity.getCompatibleModels().stream()
            .map(CarModelEntity::getId)
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    return new SparePartResponse(
        entity.getId(),
        entity.getPartName(),
        entity.getPrice(),
        ids,
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
