package ru.lab1.dealership.web.dto;

import java.util.UUID;

public record ConfigurationSelectionResponse(
    ru.lab1.dealership.domain.config.ComponentType componentType,
    UUID optionId,
    String optionName,
    java.math.BigDecimal deltaPrice) {}
