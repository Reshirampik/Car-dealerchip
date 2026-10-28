package ru.lab1.dealership.web.dto;

import java.util.UUID;

public record CreateCommonOrderRequest(UUID clientId, UUID carId) {}
