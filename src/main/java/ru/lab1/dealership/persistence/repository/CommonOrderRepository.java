package ru.lab1.dealership.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.lab1.dealership.persistence.entity.CommonOrderEntity;

public interface CommonOrderRepository extends BaseRepository<CommonOrderEntity> {
  List<CommonOrderEntity> findAllByRemovedFalseAndClient_Id(UUID clientId);

  Optional<CommonOrderEntity> findByIdAndRemovedFalseAndClient_Id(UUID id, UUID clientId);
}
