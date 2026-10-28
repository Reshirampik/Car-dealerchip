package ru.lab1.dealership.service;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.order.CommonOrderStatus;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.messaging.event.OrderSentForApprovalEvent;
import ru.lab1.dealership.messaging.event.OrderType;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.entity.CommonOrderEntity;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;
import ru.lab1.dealership.persistence.repository.CommonOrderRepository;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.CreateCommonOrderRequest;
import ru.lab1.dealership.web.dto.OrderResponse;

@Profile("order-service")
@Service
@Transactional
public class CommonOrderService {
  private final CommonOrderRepository commonOrderRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;
  private final CurrentUserService currentUserService;
  private final OutboxService outboxService;

  public CommonOrderService(
      CommonOrderRepository commonOrderRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository,
      CurrentUserService currentUserService,
      OutboxService outboxService) {
    this.commonOrderRepository = commonOrderRepository;
    this.userRepository = userRepository;
    this.carInStockRepository = carInStockRepository;
    this.currentUserService = currentUserService;
    this.outboxService = outboxService;
  }

  @Transactional(readOnly = true)
  public List<OrderResponse> list() {
    if (!currentUserService.hasAuthentication()) {
      return commonOrderRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
    }
    UserEntity currentUser = currentUserService.currentUser();
    if (isClient(currentUser)) {
      return commonOrderRepository.findAllByRemovedFalseAndClient_Id(currentUser.getId()).stream()
          .map(this::toResponse)
          .toList();
    }
    return commonOrderRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public OrderResponse get(UUID id) {
    if (!currentUserService.hasAuthentication()) {
      return toResponse(require(id));
    }
    return toResponse(requireVisible(id));
  }

  public OrderResponse create(CreateCommonOrderRequest request) {
    if (request == null || request.carId() == null) {
      throw new DomainValidationException("Invalid common order request.");
    }
    UserEntity client = resolveClient(request.clientId());
    if (!isClient(client) && !isAdmin(client)) {
      throw new DomainValidationException("Only client can create a common order.");
    }
    CarInStockEntity car =
        carInStockRepository
            .findByIdAndRemovedFalse(request.carId())
            .orElseThrow(EntityLookupSupport.notFound("Car in stock", request.carId()));
    UserEntity manager =
        userRepository.findAllByRemovedFalse().stream()
            .filter(user -> user.getRole() == UserRole.MANAGER)
            .findFirst()
            .orElseThrow(() -> new DomainValidationException("No manager available."));
    CommonOrderEntity entity = new CommonOrderEntity();
    entity.setClient(client);
    entity.setCar(car);
    entity.setManager(manager);
    entity.setTotalPrice(car.getPrice());
    entity.setStatus(CommonOrderStatus.CREATED);
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse approve(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.approveByManager();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse waitPayment(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.moveToWaitingPayment();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse markPaid(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.markPaid();
    CommonOrderEntity saved = commonOrderRepository.save(entity);
    outboxService.enqueueOrderSentForApproval(
        new OrderSentForApprovalEvent(
            UUID.randomUUID(),
            saved.getId(),
            OrderType.COMMON,
            saved.getClient().getId(),
            saved.getCar().getId(),
            null,
            Set.of(),
            outboxService.defaultTraceId(),
            Instant.now()));
    return toResponse(saved);
  }

  public OrderResponse readyForPickup(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.markReadyForPickup();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse complete(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.complete();
    return toResponse(commonOrderRepository.save(entity));
  }

  public OrderResponse cancel(UUID id) {
    CommonOrderEntity entity = requireVisible(id);
    entity.cancel();
    return toResponse(commonOrderRepository.save(entity));
  }

  public void delete(UUID id) {
    CommonOrderEntity entity = require(id);
    entity.setRemoved(true);
    commonOrderRepository.save(entity);
  }

  private CommonOrderEntity require(UUID id) {
    return commonOrderRepository
        .findByIdAndRemovedFalse(id)
        .orElseThrow(EntityLookupSupport.notFound("Common order", id));
  }

  private CommonOrderEntity requireVisible(UUID id) {
    UserEntity currentUser = currentUserService.currentUser();
    if (isClient(currentUser)) {
      return commonOrderRepository
          .findByIdAndRemovedFalseAndClient_Id(id, currentUser.getId())
          .orElseThrow(EntityLookupSupport.notFound("Common order", id));
    }
    return require(id);
  }

  private UserEntity resolveClient(UUID requestedClientId) {
    if (currentUserService.hasAuthentication()) {
      UserEntity currentUser = currentUserService.currentUser();
      if (isClient(currentUser) || isAdmin(currentUser)) {
        return currentUser;
      }
    }
    if (requestedClientId != null) {
      return userRepository
          .findByIdAndRemovedFalse(requestedClientId)
          .orElseThrow(EntityLookupSupport.notFound("User", requestedClientId));
    }
    return currentUserService.currentUser();
  }

  private boolean isClient(UserEntity user) {
    return user.getRole() == UserRole.CLIENT;
  }

  private boolean isAdmin(UserEntity user) {
    return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SYSTEM_ADMIN;
  }

  private OrderResponse toResponse(CommonOrderEntity entity) {
    return new OrderResponse(
        entity.getId(),
        entity.getStatus(),
        entity.getClient().getId(),
        entity.getManager().getId(),
        entity.getCar().getId(),
        entity.getCar().getModel().displayName(),
        entity.getTotalPrice(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
