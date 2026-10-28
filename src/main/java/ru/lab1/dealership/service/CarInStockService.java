package ru.lab1.dealership.service;

import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.web.dto.CarInStockRequest;
import ru.lab1.dealership.web.dto.CarInStockResponse;

@Profile("storage-service")
@Service
@Transactional
public class CarInStockService {
  private final CarInStockRepository carInStockRepository;
  private final CarModelRepository carModelRepository;
  private final ModelMapper modelMapper;

  public CarInStockService(
      CarInStockRepository carInStockRepository,
      CarModelRepository carModelRepository,
      ModelMapper modelMapper) {
    this.carInStockRepository = carInStockRepository;
    this.carModelRepository = carModelRepository;
    this.modelMapper = modelMapper;
  }

  @Transactional(readOnly = true)
  public List<CarInStockResponse> list() {
    return carInStockRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public CarInStockResponse get(UUID id) {
    return toResponse(
        carInStockRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", id)));
  }

  public CarInStockResponse create(CarInStockRequest request) {
    CarInStockEntity entity = toEntity(request);
    return toResponse(carInStockRepository.save(entity));
  }

  public CarInStockResponse update(UUID id, CarInStockRequest request) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", id));
    entity.setModel(
        carModelRepository
            .findByIdAndRemovedFalse(request.modelId())
            .orElseThrow(EntityLookupSupport.notFound("Car model", request.modelId())));
    entity.setColor(request.color());
    entity.setPrice(request.price());
    entity.setAvailableForTestDrive(request.availableForTestDrive());
    return toResponse(carInStockRepository.save(entity));
  }

  public void delete(UUID id) {
    CarInStockEntity entity =
        carInStockRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", id));
    entity.setRemoved(true);
    carInStockRepository.save(entity);
  }

  public CarInStockEntity requireEntity(UUID id) {
    return carInStockRepository
        .findByIdAndRemovedFalse(id)
        .orElseThrow(EntityLookupSupport.notFound("Car in stock", id));
  }

  private CarInStockEntity toEntity(CarInStockRequest request) {
    if (request == null
        || request.modelId() == null
        || request.color() == null
        || request.color().isBlank()
        || request.price() == null) {
      throw new DomainValidationException("Invalid car in stock request.");
    }
    CarInStockEntity entity = modelMapper.map(request, CarInStockEntity.class);
    entity.setModel(
        carModelRepository
            .findByIdAndRemovedFalse(request.modelId())
            .orElseThrow(EntityLookupSupport.notFound("Car model", request.modelId())));
    return entity;
  }

  private CarInStockResponse toResponse(CarInStockEntity entity) {
    return new CarInStockResponse(
        entity.getId(),
        entity.getModel().getId(),
        entity.getModel().displayName(),
        entity.getColor(),
        entity.getPrice(),
        entity.isAvailableForTestDrive(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
