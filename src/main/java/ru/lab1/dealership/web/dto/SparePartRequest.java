package ru.lab1.dealership.web.dto;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

public record SparePartRequest(String partName, BigDecimal price, Set<UUID> compatibleModelIds) {}
