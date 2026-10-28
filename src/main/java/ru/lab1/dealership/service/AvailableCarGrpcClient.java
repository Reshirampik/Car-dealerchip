package ru.lab1.dealership.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.lab1.dealership.domain.exception.EntityNotFoundException;
import ru.lab1.dealership.domain.exception.GrpcServiceUnavailableException;
import ru.lab1.dealership.grpc.CarDto;
import ru.lab1.dealership.grpc.CarIdRequest;
import ru.lab1.dealership.grpc.CarStockServiceGrpc;
import ru.lab1.dealership.grpc.CarsRequest;
import ru.lab1.dealership.web.dto.CarInStockResponse;

@Service
@Profile("order-service")
public class AvailableCarGrpcClient {
  private final CarStockServiceGrpc.CarStockServiceBlockingStub stub;
  private final long timeoutMs;

  public AvailableCarGrpcClient(
      CarStockServiceGrpc.CarStockServiceBlockingStub stub,
      @Value("${app.grpc.timeout-ms:2000}") long timeoutMs) {
    this.stub = stub;
    this.timeoutMs = timeoutMs;
  }

  public List<CarInStockResponse> listAvailableCars() {
    try {
      return stub
          .withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)
          .listAvailableCars(CarsRequest.newBuilder().build())
          .getCarsList()
          .stream()
          .map(this::toResponse)
          .toList();
    } catch (StatusRuntimeException exception) {
      throw mapGrpcError(exception);
    }
  }

  public CarInStockResponse getAvailableCar(UUID id) {
    try {
      CarDto dto =
          stub.withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS)
              .getAvailableCarById(CarIdRequest.newBuilder().setId(id.toString()).build());
      return toResponse(dto);
    } catch (StatusRuntimeException exception) {
      throw mapGrpcError(exception);
    }
  }

  private RuntimeException mapGrpcError(StatusRuntimeException exception) {
    Status.Code code = Status.fromThrowable(exception).getCode();
    if (code == Status.Code.NOT_FOUND) {
      return new EntityNotFoundException("Car not found in StorageService");
    }
    return new GrpcServiceUnavailableException("StorageService unavailable", exception);
  }

  private CarInStockResponse toResponse(CarDto dto) {
    return new CarInStockResponse(
        UUID.fromString(dto.getId()),
        UUID.fromString(dto.getModelId()),
        dto.getModelDisplayName(),
        dto.getColor(),
        new BigDecimal(dto.getPrice()),
        dto.getAvailableForTestDrive(),
        Instant.parse(dto.getCreatedAt()),
        Instant.parse(dto.getUpdatedAt()),
        dto.getRemoved());
  }
}
