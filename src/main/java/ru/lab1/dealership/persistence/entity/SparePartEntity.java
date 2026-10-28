package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "spare_parts")
public class SparePartEntity extends BaseEntity {
  @Column(name = "part_name", nullable = false)
  private String partName;

  @Column(nullable = false)
  private BigDecimal price;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "spare_part_models",
      joinColumns = @JoinColumn(name = "spare_part_id"),
      inverseJoinColumns = @JoinColumn(name = "car_model_id"))
  private Set<CarModelEntity> compatibleModels = new LinkedHashSet<>();

  public SparePartEntity() {}

  public String getPartName() {
    return partName;
  }

  public void setPartName(String partName) {
    this.partName = partName;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Set<CarModelEntity> getCompatibleModels() {
    return compatibleModels;
  }

  public void setCompatibleModels(Set<CarModelEntity> compatibleModels) {
    this.compatibleModels = compatibleModels;
  }
}
