package ru.lab1.dealership.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.car.CarInStock;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

public final class OrderService {
  private final CommonOrderRepository commonOrderRepository;
  private final UserRepository userRepository;
  private final CarInStockRepository carInStockRepository;
  private final ManagerAssignmentService managerAssignmentService;

  public OrderService(
      CommonOrderRepository commonOrderRepository,
      UserRepository userRepository,
      CarInStockRepository carInStockRepository,
      ManagerAssignmentService managerAssignmentService) {
    this.commonOrderRepository =
        Objects.requireNonNull(commonOrderRepository, "commonOrderRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    this.carInStockRepository =
        Objects.requireNonNull(carInStockRepository, "carInStockRepository");
    this.managerAssignmentService =
        Objects.requireNonNull(managerAssignmentService, "managerAssignmentService");
  }

  public CommonOrder createOrder(UUID clientId, UUID carId) {
    User client = getClient(clientId);
    CarInStock car =
        carInStockRepository
            .findById(carId)
            .orElseThrow(() -> new EntityNotFoundException("Car not found: " + carId));
    User manager = managerAssignmentService.assignManager();
    return commonOrderRepository.save(new CommonOrder(UUID.randomUUID(), client, manager, car));
  }

  public CommonOrder approveByManager(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.approveByManager();
    return commonOrderRepository.save(order);
  }

  public CommonOrder moveToWaitingPayment(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.moveToWaitingPayment();
    return commonOrderRepository.save(order);
  }

  public CommonOrder markPaid(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.markPaid();
    return commonOrderRepository.save(order);
  }

  public CommonOrder markReadyForPickup(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.markReadyForPickup();
    return commonOrderRepository.save(order);
  }

  public CommonOrder complete(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.complete();
    return commonOrderRepository.save(order);
  }

  public CommonOrder cancel(UUID orderId) {
    CommonOrder order = getOrder(orderId);
    order.cancel();
    return commonOrderRepository.save(order);
  }

  public CommonOrder getOrder(UUID orderId) {
    return commonOrderRepository
        .findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Common order not found: " + orderId));
  }

  public List<CommonOrder> listOrders() {
    return commonOrderRepository.findAll();
  }

  private User getClient(UUID clientId) {
    User client =
        userRepository
            .findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + clientId));
    if (!client.isClient()) {
      throw new DomainValidationException("Only a client can create a car order.");
    }
    return client;
  }
}
