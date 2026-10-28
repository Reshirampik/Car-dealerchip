package ru.lab1.dealership.infrastructure.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import ru.lab1.dealership.domain.Identifiable;

public abstract class AbstractInMemoryRepository<T extends Identifiable> {
  private final Map<UUID, T> storage = new LinkedHashMap<>();

  public T save(T entity) {
    storage.put(entity.getId(), entity);
    return entity;
  }

  public Optional<T> findById(UUID id) {
    return Optional.ofNullable(storage.get(id));
  }

  public List<T> findAll() {
    return List.copyOf(new ArrayList<>(storage.values()));
  }

  public void delete(UUID id) {
    storage.remove(id);
  }
}
