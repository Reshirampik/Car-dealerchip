package ru.lab1.dealership.domain.config;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import ru.lab1.dealership.domain.car.CarModel;
import ru.lab1.dealership.domain.exception.DomainValidationException;
import ru.lab1.dealership.domain.exception.IncompatibleComponentException;

public final class CarConfiguration {
  private final CarModel model;
  private final Map<ComponentType, ComponentOption> selectedOptions =
      new EnumMap<>(ComponentType.class);

  public CarConfiguration(CarModel model) {
    this.model = Objects.requireNonNull(model, "model");
  }

  public CarModel getModel() {
    return model;
  }

  public void selectOption(ComponentOption option) {
    Objects.requireNonNull(option, "option");
    if (!option.isCompatible(model.getId())) {
      throw new IncompatibleComponentException(
          "Выбранный компонент «"
              + option.getName()
              + "» недоступен для модели "
              + model.displayName()
              + '.');
    }
    selectedOptions.put(option.getType(), option);
  }

  public Map<ComponentType, ComponentOption> getSelectedOptions() {
    return Map.copyOf(selectedOptions);
  }

  public BigDecimal totalPrice() {
    validateCompleteConfiguration();
    return model
        .getBasePrice()
        .add(
            selectedOptions.values().stream()
                .map(ComponentOption::getDeltaPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
  }

  private void validateCompleteConfiguration() {
    for (ComponentType type : ComponentType.values()) {
      if (!selectedOptions.containsKey(type)) {
        throw new DomainValidationException(
            "Отсутствует обязательный узел «" + humanName(type) + "».");
      }
    }
  }

  private static String humanName(ComponentType type) {
    return switch (type) {
      case WHEELS -> "Колёса";
      case TRANSMISSION -> "Трансмиссия";
      case STEERING_WHEEL -> "Руль";
      case INTERIOR -> "Интерьер салона";
    };
  }
}
