package ru.lab1.dealership.web.dto;

import java.util.UUID;

public record ComponentSelectionRequest(
    ru.lab1.dealership.domain.config.ComponentType componentType, UUID optionId) {}
