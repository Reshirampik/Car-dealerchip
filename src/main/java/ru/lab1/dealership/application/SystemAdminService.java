package ru.lab1.dealership.application;

import java.util.List;
import java.util.UUID;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.order.CommonOrder;
import ru.lab1.dealership.domain.order.CustomOrder;
import ru.lab1.dealership.domain.testdrive.TestDriveRequest;
import ru.lab1.dealership.domain.user.User;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.infrastructure.repository.CarInStockRepository;
import ru.lab1.dealership.infrastructure.repository.CarModelRepository;
import ru.lab1.dealership.infrastructure.repository.CommonOrderRepository;
import ru.lab1.dealership.infrastructure.repository.CustomOrderRepository;
import ru.lab1.dealership.infrastructure.repository.SparePartRepository;
import ru.lab1.dealership.infrastructure.repository.TestDriveRequestRepository;
import ru.lab1.dealership.infrastructure.repository.UserRepository;

public final class SystemAdminService extends WarehouseService {
  private final UserRepository userRepository;
  private final CommonOrderRepository commonOrderRepository;
  private final CustomOrderRepository customOrderRepository;
  private final TestDriveRequestRepository testDriveRequestRepository;

  public SystemAdminService(
      CarModelRepository carModelRepository,
      CarInStockRepository carInStockRepository,
      SparePartRepository sparePartRepository,
      UserRepository userRepository,
      CommonOrderRepository commonOrderRepository,
      CustomOrderRepository customOrderRepository,
      TestDriveRequestRepository testDriveRequestRepository) {
    super(carModelRepository, carInStockRepository, sparePartRepository);
    this.userRepository = userRepository;
    this.commonOrderRepository = commonOrderRepository;
    this.customOrderRepository = customOrderRepository;
    this.testDriveRequestRepository = testDriveRequestRepository;
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public User updateUser(UUID id, String fullName, UserRole role) {
    User user = getUser(id);
    user.rename(fullName);
    user.changeRole(role);
    return userRepository.save(user);
  }

  public User getUser(UUID id) {
    return userRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + id));
  }

  public List<User> listUsers() {
    return userRepository.findAll();
  }

  public void deleteUser(UUID id) {
    userRepository.delete(id);
  }

  public CommonOrder saveCommonOrder(CommonOrder order) {
    return commonOrderRepository.save(order);
  }

  public CommonOrder getCommonOrder(UUID id) {
    return commonOrderRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Common order not found: " + id));
  }

  public List<CommonOrder> listCommonOrders() {
    return commonOrderRepository.findAll();
  }

  public void deleteCommonOrder(UUID id) {
    commonOrderRepository.delete(id);
  }

  public CustomOrder saveCustomOrder(CustomOrder order) {
    return customOrderRepository.save(order);
  }

  public CustomOrder getCustomOrder(UUID id) {
    return customOrderRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Custom order not found: " + id));
  }

  public List<CustomOrder> listCustomOrders() {
    return customOrderRepository.findAll();
  }

  public void deleteCustomOrder(UUID id) {
    customOrderRepository.delete(id);
  }

  public TestDriveRequest saveTestDriveRequest(TestDriveRequest request) {
    return testDriveRequestRepository.save(request);
  }

  public TestDriveRequest getTestDriveRequest(UUID id) {
    return testDriveRequestRepository
        .findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Test drive request not found: " + id));
  }

  public List<TestDriveRequest> listTestDriveRequests() {
    return testDriveRequestRepository.findAll();
  }

  public void deleteTestDriveRequest(UUID id) {
    testDriveRequestRepository.delete(id);
  }
}
