package ru.lab1.dealership.service;

import java.util.Collection;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.user.UserRole;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.UserRepository;

@Profile("order-service")
@Service
@Transactional
public class CurrentUserService {
  private final UserRepository userRepository;

  public CurrentUserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean hasAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication instanceof JwtAuthenticationToken jwtAuthenticationToken
        && jwtAuthenticationToken.isAuthenticated();
  }

  public UserEntity currentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
      return fromJwt(jwtAuthenticationToken.getToken(), jwtAuthenticationToken.getAuthorities());
    }
    return fallbackClient();
  }

  public boolean hasRole(UserRole role) {
    return currentUser().getRole() == role
        || (role == UserRole.ADMIN && currentUser().getRole() == UserRole.SYSTEM_ADMIN);
  }

  private UserEntity fromJwt(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
    String keycloakId = jwt.getSubject();
    Optional<UserEntity> existing = userRepository.findByKeycloakIdAndRemovedFalse(keycloakId);
    if (existing.isPresent()) {
      return existing.get();
    }

    UserRole role = inferRole(authorities);
    String fullName =
        firstNonBlank(
            jwt.getClaimAsString("name"), jwt.getClaimAsString("preferred_username"), keycloakId);
    UserEntity created = new UserEntity(keycloakId, fullName, role);
    return userRepository.save(created);
  }

  private UserRole inferRole(Collection<? extends GrantedAuthority> authorities) {
    if (hasAuthority(authorities, "ROLE_ADMIN")) {
      return UserRole.ADMIN;
    }
    if (hasAuthority(authorities, "ROLE_MANAGER")) {
      return UserRole.MANAGER;
    }
    if (hasAuthority(authorities, "ROLE_WAREHOUSE_ADMIN")) {
      return UserRole.WAREHOUSE_ADMIN;
    }
    return UserRole.CLIENT;
  }

  private boolean hasAuthority(
      Collection<? extends GrantedAuthority> authorities, String authority) {
    return authorities.stream().anyMatch(item -> authority.equals(item.getAuthority()));
  }

  private UserEntity fallbackClient() {
    return userRepository.findAllByRemovedFalse().stream()
        .filter(user -> user.getRole() == UserRole.CLIENT)
        .findFirst()
        .orElseGet(() -> userRepository.save(new UserEntity("local-client", UserRole.CLIENT)));
  }

  private String firstNonBlank(String... values) {
    for (String value : values) {
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return "unknown";
  }
}
