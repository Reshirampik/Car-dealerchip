package ru.lab1.dealership.web.dto;

import java.util.Set;
import java.util.UUID;

public record ConfigurationRequest(UUID modelId, Set<ComponentSelectionRequest> selections) {}
