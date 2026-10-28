package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import ru.lab1.dealership.domain.config.ComponentType;

@Entity
@Table(name = "component_options")
public class ComponentOptionEntity extends BaseEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "component_type", nullable = false)
  private ComponentType componentType;

  @Column(name = "option_name", nullable = false)
  private String optionName;

  @Column(name = "delta_price", nullable = false)
  private BigDecimal deltaPrice;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "component_option_models",
      joinColumns = @JoinColumn(name = "component_option_id"),
      inverseJoinColumns = @JoinColumn(name = "car_model_id"))
  private Set<CarModelEntity> compatibleModels = new LinkedHashSet<>();

  public ComponentOptionEntity() {}

  public ComponentType getComponentType() {
    return componentType;
  }

  public void setComponentType(ComponentType componentType) {
    this.componentType = componentType;
  }

  public String getOptionName() {
    return optionName;
  }

  public void setOptionName(String optionName) {
    this.optionName = optionName;
  }

  public BigDecimal getDeltaPrice() {
    return deltaPrice;
  }

  public void setDeltaPrice(BigDecimal deltaPrice) {
    this.deltaPrice = deltaPrice;
  }

  public Set<CarModelEntity> getCompatibleModels() {
    return compatibleModels;
  }

  public void setCompatibleModels(Set<CarModelEntity> compatibleModels) {
    this.compatibleModels = compatibleModels;
  }
}
