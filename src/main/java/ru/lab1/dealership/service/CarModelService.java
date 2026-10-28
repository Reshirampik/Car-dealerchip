package ru.lab1.dealership.service;

import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.specification.CarModelSpecification;
import ru.lab1.dealership.web.dto.CarModelFilterRequest;
import ru.lab1.dealership.web.dto.CarModelRequest;
import ru.lab1.dealership.web.dto.CarModelResponse;

@Profile("storage-service")
@Service
@Transactional
public class CarModelService {
  private final CarModelRepository carModelRepository;
  private final ModelMapper modelMapper;

  public CarModelService(CarModelRepository carModelRepository, ModelMapper modelMapper) {
    this.carModelRepository = carModelRepository;
    this.modelMapper = modelMapper;
  }

  @Transactional(readOnly = true)
  public List<CarModelResponse> list(CarModelFilterRequest filter) {
    Specification<CarModelEntity> specification = CarModelSpecification.fromFilter(filter);
    return carModelRepository.findAll(specification).stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public CarModelResponse get(UUID id) {
    return toResponse(
        carModelRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car model", id)));
  }

  public CarModelResponse create(CarModelRequest request) {
    validateRequest(request);
    CarModelEntity entity = toEntity(request);
    return toResponse(carModelRepository.save(entity));
  }

  public CarModelResponse update(UUID id, CarModelRequest request) {
    validateRequest(request);
    CarModelEntity entity =
        carModelRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car model", id));
    modelMapper.map(request, entity);
    entity.setRequiredComponentTypes(request.requiredComponentTypes());
    return toResponse(carModelRepository.save(entity));
  }

  public void delete(UUID id) {
    CarModelEntity entity =
        carModelRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car model", id));
    entity.setRemoved(true);
    carModelRepository.save(entity);
  }

  public CarModelEntity requireEntity(UUID id) {
    return carModelRepository
        .findByIdAndRemovedFalse(id)
        .orElseThrow(EntityLookupSupport.notFound("Car model", id));
  }

  private void validateRequest(CarModelRequest request) {
    if (request == null
        || request.brand() == null
        || request.brand().isBlank()
        || request.modelName() == null
        || request.modelName().isBlank()
        || request.basePrice() == null
        || request.requiredComponentTypes() == null
        || request.requiredComponentTypes().isEmpty()) {
      throw new DomainValidationException("Invalid car model request.");
    }
  }

  private CarModelEntity toEntity(CarModelRequest request) {
    CarModelEntity entity = modelMapper.map(request, CarModelEntity.class);
    entity.setRequiredComponentTypes(request.requiredComponentTypes());
    return entity;
  }

  private CarModelResponse toResponse(CarModelEntity entity) {
    return new CarModelResponse(
        entity.getId(),
        entity.getBrand(),
        entity.getModelName(),
        entity.getBodyType(),
        entity.getFuelType(),
        entity.getEnginePowerHp(),
        entity.getEngineVolumeLiters(),
        entity.getGearboxType(),
        entity.getDriveType(),
        entity.getBasePrice(),
        entity.getRequiredComponentTypes(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
