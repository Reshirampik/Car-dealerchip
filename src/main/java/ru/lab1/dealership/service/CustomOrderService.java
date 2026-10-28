package ru.lab1.dealership.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.config.ComponentType;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.IncompatibleComponentException;
import ru.lab1.dealership.domain.order.CustomOrderStatus;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.messaging.event.OrderSentForApprovalEvent;
import ru.lab1.dealership.messaging.event.OrderType;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.persistence.entity.ComponentOptionEntity;
import ru.lab1.dealership.persistence.entity.CustomOrderEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarModelRepository;
import ru.lab1.dealership.persistence.repository.ComponentOptionRepository;
import ru.lab1.dealership.persistence.repository.CustomOrderRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.ComponentSelectionRequest;
import ru.lab1.dealership.web.dto.ConfigurationSelectionResponse;
import ru.lab1.dealership.web.dto.CreateCustomOrderRequest;
import ru.lab1.dealership.web.dto.CustomOrderResponse;

@Profile("order-service")
@Service
@Transactional
public class CustomOrderService {
  private final CustomOrderRepository customOrderRepository;
  private final UserRepository userRepository;
  private final CarModelRepository carModelRepository;
  private final ComponentOptionRepository componentOptionRepository;
  private final OutboxService outboxService;

  public CustomOrderService(
      CustomOrderRepository customOrderRepository,
      UserRepository userRepository,
      CarModelRepository carModelRepository,
      ComponentOptionRepository componentOptionRepository,
      OutboxService outboxService) {
    this.customOrderRepository = customOrderRepository;
    this.userRepository = userRepository;
    this.carModelRepository = carModelRepository;
    this.componentOptionRepository = componentOptionRepository;
    this.outboxService = outboxService;
  }

  @Transactional(readOnly = true)
  public List<CustomOrderResponse> list() {
    return customOrderRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public CustomOrderResponse get(UUID id) {
    return toResponse(
        customOrderRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("Custom order", id)));
  }

  public CustomOrderResponse create(CreateCustomOrderRequest request) {
    if (request == null
        || request.clientId() == null
        || request.modelId() == null
        || request.selections() == null
        || request.selections().isEmpty()) {
      throw new DomainValidationException("Invalid custom order request.");
    }

    UserEntity client =
        userRepository
            .findByIdAndRemovedFalse(request.clientId())
            .orElseThrow(EntityLookupSupport.notFound("User", request.clientId()));
    if (client.getRole() != UserRole.CLIENT) {
      throw new DomainValidationException("Only client can create a custom order.");
    }

    CarModelEntity model =
        carModelRepository
            .findByIdAndRemovedFalse(request.modelId())
            .orElseThrow(EntityLookupSupport.notFound("Car model", request.modelId()));

    UserEntity manager =
        userRepository.findAllByRemovedFalse().stream()
            .filter(user -> user.getRole() == UserRole.MANAGER)
            .findFirst()
            .orElseThrow(() -> new DomainValidationException("No manager available."));

    Set<ComponentOptionEntity> selectedOptions = resolveSelections(model, request.selections());

    CustomOrderEntity entity = new CustomOrderEntity();
    entity.setClient(client);
    entity.setManager(manager);
    entity.setModel(model);
    entity.setSelectedOptions(selectedOptions);
    entity.setTotalPrice(calculateTotalPrice(model, selectedOptions));
    entity.setStatus(CustomOrderStatus.CREATED);
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse approve(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.approveByWarehouse();
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse waitPayment(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.moveToWaitingPayment();
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse markPaid(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.markPaid();
    CustomOrderEntity saved = customOrderRepository.save(entity);
    outboxService.enqueueOrderSentForApproval(
        new OrderSentForApprovalEvent(
            UUID.randomUUID(),
            saved.getId(),
            OrderType.CUSTOM,
            saved.getClient().getId(),
            null,
            saved.getModel().getId(),
            saved.getSelectedOptions().stream()
                .map(ComponentOptionEntity::getId)
                .collect(java.util.stream.Collectors.toSet()),
            outboxService.defaultTraceId(),
            Instant.now()));
    return toResponse(saved);
  }

  public CustomOrderResponse waitDelivery(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.markWaitingDelivery();
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse readyForPickup(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.markReadyForPickup();
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse complete(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.complete();
    return toResponse(customOrderRepository.save(entity));
  }

  public CustomOrderResponse cancel(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.cancel();
    return toResponse(customOrderRepository.save(entity));
  }

  public void delete(UUID id) {
    CustomOrderEntity entity = require(id);
    entity.setRemoved(true);
    customOrderRepository.save(entity);
  }

  private CustomOrderEntity require(UUID id) {
    return customOrderRepository
        .findByIdAndRemovedFalse(id)
        .orElseThrow(EntityLookupSupport.notFound("Custom order", id));
  }

  private Set<ComponentOptionEntity> resolveSelections(
      CarModelEntity model, Set<ComponentSelectionRequest> selections) {
    Set<ComponentOptionEntity> result = new LinkedHashSet<>();
    for (ComponentSelectionRequest selection : selections) {
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
      result.add(option);
    }
    for (ComponentType required : model.getRequiredComponentTypes()) {
      boolean present = result.stream().anyMatch(option -> option.getComponentType() == required);
      if (!present) {
        throw new DomainValidationException("Missing required component: " + required);
      }
    }
    return result;
  }

  private BigDecimal calculateTotalPrice(
      CarModelEntity model, Set<ComponentOptionEntity> selectedOptions) {
    BigDecimal total = model.getBasePrice();
    for (ComponentOptionEntity option : selectedOptions) {
      total = total.add(option.getDeltaPrice());
    }
    return total;
  }

  private CustomOrderResponse toResponse(CustomOrderEntity entity) {
    List<ConfigurationSelectionResponse> selections = new ArrayList<>();
    for (ComponentOptionEntity option : entity.getSelectedOptions()) {
      selections.add(
          new ConfigurationSelectionResponse(
              option.getComponentType(),
              option.getId(),
              option.getOptionName(),
              option.getDeltaPrice()));
    }
    return new CustomOrderResponse(
        entity.getId(),
        entity.getStatus(),
        entity.getClient().getId(),
        entity.getManager().getId(),
        entity.getModel().getId(),
        entity.getModel().displayName(),
        entity.getTotalPrice(),
        selections,
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
