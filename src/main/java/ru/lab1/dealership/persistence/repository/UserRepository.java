package ru.lab1.dealership.persistence.repository;

import java.util.Optional;
import ru.lab1.dealership.persistence.entity.UserEntity;

public interface UserRepository extends BaseRepository<UserEntity> {
  Optional<UserEntity> findByKeycloakIdAndRemovedFalse(String keycloakId);
}
