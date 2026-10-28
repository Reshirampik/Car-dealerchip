package ru.lab1.dealership.service;

import java.util.UUID;
import java.util.function.Supplier;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;

final class EntityLookupSupport {
  private EntityLookupSupport() {}

  static <T> T requireFound(T value, UUID id, String entityName) {
    if (value == null) {
      throw new EntityNotFoundException(entityName + " not found: " + id);
    }
    return value;
  }

  static Supplier<EntityNotFoundException> notFound(String entityName, UUID id) {
    return () -> new EntityNotFoundException(entityName + " not found: " + id);
  }
}
