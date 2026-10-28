package ru.lab1.dealership.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import ru.lab1.dealership.persistence.entity.BaseEntity;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity>
    extends JpaRepository<T, UUID>, JpaSpecificationExecutor<T> {
  Optional<T> findByIdAndRemovedFalse(UUID id);

  List<T> findAllByRemovedFalse();
}
