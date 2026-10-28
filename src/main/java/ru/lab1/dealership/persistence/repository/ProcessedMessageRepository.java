package ru.lab1.dealership.persistence.repository;

import java.util.Optional;
import java.util.UUID;
import ru.lab1.dealership.persistence.entity.ProcessedMessageEntity;

public interface ProcessedMessageRepository extends BaseRepository<ProcessedMessageEntity> {
  Optional<ProcessedMessageEntity> findByConsumerNameAndMessageIdAndRemovedFalse(
      String consumerName, UUID messageId);
}
