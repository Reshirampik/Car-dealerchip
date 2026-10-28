package ru.lab1.dealership.service;

import java.util.List;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.persistence.entity.UserEntity;
import ru.lab1.dealership.persistence.repository.UserRepository;
import ru.lab1.dealership.web.dto.UserRequest;
import ru.lab1.dealership.web.dto.UserResponse;

@Profile("order-service")
@Service
@Transactional
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<UserResponse> list() {
    return userRepository.findAllByRemovedFalse().stream().map(this::toResponse).toList();
  }

  @Transactional(readOnly = true)
  public UserResponse get(UUID id) {
    return toResponse(
        userRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("User", id)));
  }

  public UserResponse create(UserRequest request) {
    validateRequest(request);
    UserEntity entity = new UserEntity(request.keycloakId(), request.fullName(), request.role());
    return toResponse(userRepository.save(entity));
  }

  public UserResponse update(UUID id, UserRequest request) {
    validateRequest(request);
    UserEntity entity =
        userRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("User", id));
    entity.setKeycloakId(request.keycloakId());
    entity.setFullName(request.fullName());
    entity.setRole(request.role());
    return toResponse(userRepository.save(entity));
  }

  public void delete(UUID id) {
    UserEntity entity =
        userRepository
            .findByIdAndRemovedFalse(id)
            .orElseThrow(EntityLookupSupport.notFound("User", id));
    entity.setRemoved(true);
    userRepository.save(entity);
  }

  private void validateRequest(UserRequest request) {
    if (request == null
        || request.fullName() == null
        || request.fullName().isBlank()
        || request.role() == null) {
      throw new DomainValidationException("Invalid user request.");
    }
  }

  private UserResponse toResponse(UserEntity entity) {
    return new UserResponse(
        entity.getId(),
        entity.getKeycloakId(),
        entity.getFullName(),
        entity.getRole(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.isRemoved());
  }
}
