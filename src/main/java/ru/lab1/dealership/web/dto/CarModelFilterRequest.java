package ru.lab1.dealership.web.dto;

import java.util.Set;
import ru.lab1.dealership.domain.config.ComponentType;

public record CarModelFilterRequest(String brand, Set<ComponentType> componentTypes) {}
