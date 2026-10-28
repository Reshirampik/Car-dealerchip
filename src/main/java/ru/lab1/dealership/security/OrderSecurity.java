package ru.lab1.dealership.security;

import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.repository.CommonOrderRepository;
import ru.lab1.dealership.persistence.repository.CustomOrderRepository;
import ru.lab1.dealership.service.CurrentUserService;

@Profile("order-service")
@Component("orderSecurity")
public class OrderSecurity {
  private final CurrentUserService currentUserService;
  private final CommonOrderRepository commonOrderRepository;
  private final CustomOrderRepository customOrderRepository;

  public OrderSecurity(
      CurrentUserService currentUserService,
      CommonOrderRepository commonOrderRepository,
      CustomOrderRepository customOrderRepository) {
    this.currentUserService = currentUserService;
    this.commonOrderRepository = commonOrderRepository;
    this.customOrderRepository = customOrderRepository;
  }

  @Transactional(readOnly = true)
  public boolean canViewCommonOrder(UUID orderId) {
    if (!isAuthenticated()) {
      return true;
    }
    if (isManagerOrAdmin()) {
      return true;
    }
    var currentUser = currentUserService.currentUser();
    return commonOrderRepository
        .findByIdAndRemovedFalseAndClient_Id(orderId, currentUser.getId())
        .isPresent();
  }

  @Transactional(readOnly = true)
  public boolean canCancelCommonOrder(UUID orderId) {
    return canViewCommonOrder(orderId);
  }

  @Transactional(readOnly = true)
  public boolean canViewCustomOrder(UUID orderId) {
    if (!isAuthenticated()) {
      return true;
    }
    if (isManagerOrAdmin()) {
      return true;
    }
    var currentUser = currentUserService.currentUser();
    return customOrderRepository
        .findByIdAndRemovedFalseAndClient_Id(orderId, currentUser.getId())
        .isPresent();
  }

  @Transactional(readOnly = true)
  public boolean canCancelCustomOrder(UUID orderId) {
    return canViewCustomOrder(orderId);
  }

  private boolean isAuthenticated() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.isAuthenticated()
        && !(authentication
            instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
  }

  private boolean isManagerOrAdmin() {
    if (!isAuthenticated()) {
      return false;
    }
    var role = currentUserService.currentUser().getRole();
    return role == UserRole.MANAGER || role == UserRole.ADMIN || role == UserRole.SYSTEM_ADMIN;
  }
}
