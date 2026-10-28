package ru.lab1.dealership.service;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.order.AssemblyOrderStatus;
import ru.lab1.dealership.persistence.entity.AssemblyOrderEntity;
import ru.lab1.dealership.persistence.repository.AssemblyOrderRepository;
import ru.lab1.dealership.web.dto.AssemblyOrderRequest;
import ru.lab1.dealership.web.dto.AssemblyOrderResponse;

@Profile("storage-service")
@Service
@Transactional
public class AssemblyOrderService {
  private final AssemblyOrderRepository assemblyOrderRepository;

  public AssemblyOrderService(AssemblyOrderRepository assemblyOrderRepository) {
    this.assemblyOrderRepository = assemblyOrderRepository;
  }

  @Transactional(readOnly = true)
  public List<AssemblyOrderResponse> list() {
    return assemblyOrderRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public AssemblyOrderResponse get(UUID id) {
    return toResponse(
        assemblyOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Assembly order", id)));
  }

  public AssemblyOrderResponse create(AssemblyOrderRequest request) {
    validate(request);
    AssemblyOrderEntity entity = new AssemblyOrderEntity();
    apply(entity, request);
    return toResponse(assemblyOrderRepository.save(entity));
  }

  public AssemblyOrderResponse update(UUID id, AssemblyOrderRequest request) {
    validate(request);
    AssemblyOrderEntity entity =
        assemblyOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Assembly order", id));
    apply(entity, request);
    return toResponse(assemblyOrderRepository.save(entity));
  }

  public void delete(UUID id) {
    AssemblyOrderEntity entity =
        assemblyOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Assembly order", id));
    entity.setRemoved(true);
    assemblyOrderRepository.save(entity);
  }

  public AssemblyOrderResponse assemble(UUID id) {
    AssemblyOrderEntity entity =
        assemblyOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Assembly order", id));
    entity.setStatus(AssemblyOrderStatus.ASSEMBLED);
    return toResponse(assemblyOrderRepository.save(entity));
  }

  public AssemblyOrderResponse fail(UUID id) {
    AssemblyOrderEntity entity =
        assemblyOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Assembly order", id));
    entity.setStatus(AssemblyOrderStatus.FAIL);
    return toResponse(assemblyOrderRepository.save(entity));
  }

  private void validate(AssemblyOrderRequest request) {
    if (request == null
        || request.sourceOrderId() == null
        || request.sourceOrderType() == null
        || request.status() == null) {
      throw new DomainValidationException("Invalid assembly order request.");
    }
  }

  private void apply(AssemblyOrderEntity entity, AssemblyOrderRequest request) {
    entity.setSourceOrderId(request.sourceOrderId());
    entity.setSourceOrderType(request.sourceOrderType());
    entity.setCarId(request.carId());
    entity.setModelId(request.modelId());
    entity.setRequiredComponentIds(request.requiredComponentIds());
    entity.setWarehouseEmployeeId(request.warehouseEmployeeId());
    entity.setStatus(AssemblyOrderStatus.valueOf(request.status()));
  }

  private AssemblyOrderResponse toResponse(AssemblyOrderEntity entity) {
    return new AssemblyOrderResponse(
        entity.getId(),
        entity.getSourceOrderId(),
        entity.getSourceOrderType(),
        entity.getCarId(),
        entity.getModelId(),
        entity.getRequiredComponentIds(),
        entity.getWarehouseEmployeeId(),
        entity.getStatus().name(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
