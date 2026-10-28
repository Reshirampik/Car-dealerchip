package ru.lab1.dealership.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import ru.lab1.dealership.persistence.entity.TestDriveRequestEntity;

public interface TestDriveRequestRepository extends BaseRepository<TestDriveRequestEntity> {
  List<TestDriveRequestEntity> findAllByRemovedFalseAndClient_Id(UUID clientId);

  Optional<TestDriveRequestEntity> findByIdAndRemovedFalseAndClient_Id(UUID id, UUID clientId);
}
