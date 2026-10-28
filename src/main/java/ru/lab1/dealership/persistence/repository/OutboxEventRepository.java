package ru.lab1.dealership.persistence.repository;

import java.util.List;
import ru.lab1.dealership.domain.order.OutboxEventStatus;
import ru.lab1.dealership.persistence.entity.OutboxEventEntity;

public interface OutboxEventRepository extends BaseRepository<OutboxEventEntity> {
  List<OutboxEventEntity> findAllByRemovedFalseAndStatus(OutboxEventStatus status);
}
