package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record ComponentOptionRequest(
    ru.lab1.dealership.domain.config.ComponentType componentType,
    String optionName,
    BigDecimal deltaPrice,
    Set<UUID> compatibleModelIds) {}
