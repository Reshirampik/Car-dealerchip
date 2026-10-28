package ru.lab1.dealership.persistence.specification;

import jakarta.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import ru.lab1.dealership.persistence.entity.CarModelEntity;
import ru.lab1.dealership.web.dto.CarModelFilterRequest;

public final class CarModelSpecification {
  private CarModelSpecification() {}

  public static Specification<CarModelEntity> fromFilter(CarModelFilterRequest filter) {
    return (root, query, cb) -> {
      List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
      predicates.add(cb.isFalse(root.get("removed")));
      if (filter == null) {
        query.distinct(true);
        return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
      }
      if (filter.brand() != null && !filter.brand().isBlank()) {
        predicates.add(cb.equal(cb.lower(root.get("brand")), filter.brand().toLowerCase()));
      }
      if (filter.componentTypes() != null && !filter.componentTypes().isEmpty()) {
        var join = root.join("requiredComponentTypes", JoinType.INNER);
        predicates.add(join.in(filter.componentTypes()));
        query.distinct(true);
      }
      return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
    };
  }
}
