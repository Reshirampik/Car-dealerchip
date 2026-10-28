package ru.lab1.dealership.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.lab1.dealership.persistence.entity.CustomOrderEntity;

public interface CustomOrderRepository extends BaseRepository<CustomOrderEntity> {
  List<CustomOrderEntity> findAllByRemovedFalseAndClient_Id(UUID clientId);

  Optional<CustomOrderEntity> findByIdAndRemovedFalseAndClient_Id(UUID id, UUID clientId);
}
