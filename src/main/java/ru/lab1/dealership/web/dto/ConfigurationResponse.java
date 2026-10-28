package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ConfigurationResponse(
    UUID modelId,
    String modelDisplayName,
    BigDecimal basePrice,
    BigDecimal totalPrice,
    List<ConfigurationSelectionResponse> selections) {}
