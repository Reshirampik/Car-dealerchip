package ru.lab1.dealership.application;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.config.CarConfiguration;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

public final class CustomOrderService {
  private final CustomOrderRepository customOrderRepository;
  private final UserRepository userRepository;
  private final CarModelRepository carModelRepository;
  private final ManagerAssignmentService managerAssignmentService;

  public CustomOrderService(
      CustomOrderRepository customOrderRepository,
      UserRepository userRepository,
      CarModelRepository carModelRepository,
      ManagerAssignmentService managerAssignmentService) {
    this.customOrderRepository =
        Objects.requireNonNull(customOrderRepository, "customOrderRepository");
    this.userRepository = Objects.requireNonNull(userRepository, "userRepository");
    this.carModelRepository = Objects.requireNonNull(carModelRepository, "carModelRepository");
    this.managerAssignmentService =
        Objects.requireNonNull(managerAssignmentService, "managerAssignmentService");
  }

  public CustomOrder createOrder(UUID clientId, CarConfiguration configuration) {
    User client = getClient(clientId);
    validateConfiguration(configuration);
    User manager = managerAssignmentService.assignManager();
    return customOrderRepository.save(
        new CustomOrder(UUID.randomUUID(), client, manager, configuration));
  }

  public CustomOrder approveByWarehouse(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.approveByWarehouse();
    return customOrderRepository.save(order);
  }

  public CustomOrder moveToWaitingPayment(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.moveToWaitingPayment();
    return customOrderRepository.save(order);
  }

  public CustomOrder markPaid(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.markPaid();
    return customOrderRepository.save(order);
  }

  public CustomOrder moveToWaitingDelivery(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.moveToWaitingDelivery();
    return customOrderRepository.save(order);
  }

  public CustomOrder markReadyForPickup(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.markReadyForPickup();
    return customOrderRepository.save(order);
  }

  public CustomOrder complete(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.complete();
    return customOrderRepository.save(order);
  }

  public CustomOrder cancel(UUID orderId) {
    CustomOrder order = getOrder(orderId);
    order.cancel();
    return customOrderRepository.save(order);
  }

  public CustomOrder getOrder(UUID orderId) {
    return customOrderRepository
        .findById(orderId)
        .orElseThrow(() -> new EntityNotFoundException("Custom order not found: " + orderId));
  }

  public List<CustomOrder> listOrders() {
    return customOrderRepository.findAll();
  }

  private User getClient(UUID clientId) {
    User client =
        userRepository
            .findById(clientId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + clientId));
    if (!client.isClient()) {
      throw new DomainValidationException("Only a client can create a custom order.");
    }
    return client;
  }

  private void validateConfiguration(CarConfiguration configuration) {
    CarModel model = configuration.getModel();
    if (carModelRepository.findById(model.getId()).isEmpty()) {
      throw new EntityNotFoundException("Car model not found: " + model.getId());
    }
  }
}
