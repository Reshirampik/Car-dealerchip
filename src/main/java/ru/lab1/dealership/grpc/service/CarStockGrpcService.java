package ru.lab1.dealership.grpc.service;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.grpc.CarDto;
import ru.lab1.dealership.grpc.CarIdRequest;
import ru.lab1.dealership.grpc.CarListResponse;
import ru.lab1.dealership.grpc.CarStockServiceGrpc;
import ru.lab1.dealership.grpc.CarsRequest;
import ru.lab1.dealership.persistence.entity.CarInStockEntity;
import ru.lab1.dealership.persistence.repository.CarInStockRepository;

@Service
@Profile("storage-service")
public class CarStockGrpcService extends CarStockServiceGrpc.CarStockServiceImplBase {
  private static final Logger log = LoggerFactory.getLogger(CarStockGrpcService.class);

  private final CarInStockRepository carInStockRepository;

  public CarStockGrpcService(CarInStockRepository carInStockRepository) {
    this.carInStockRepository = carInStockRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public void listAvailableCars(
      CarsRequest request, StreamObserver<CarListResponse> responseObserver) {
    log.info("gRPC request: ListAvailableCars");
    try {
      CarListResponse response =
          CarListResponse.newBuilder()
              .addAllCars(
                  carInStockRepository.findAllByRemovedFalse().stream()
                      .filter(car -> car.getModel() != null)
                      .map(this::toDto)
                      .toList())
              .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
      log.info("gRPC response: ListAvailableCars size={}", response.getCarsCount());
    } catch (Exception exception) {
      log.error("gRPC error in ListAvailableCars", exception);
      responseObserver.onError(
          Status.INTERNAL
              .withDescription(exception.getMessage())
              .withCause(exception)
              .asRuntimeException());
    }
  }

  @Override
  @Transactional(readOnly = true)
  public void getAvailableCarById(CarIdRequest request, StreamObserver<CarDto> responseObserver) {
    log.info("gRPC request: GetAvailableCarById id={}", request.getId());
    try {
      UUID id = UUID.fromString(request.getId());
      CarDto dto =
          toDto(
              carInStockRepository
                  .findByIdAndRemovedFalse(id)
                  .orElseThrow(
                      () ->
                          Status.NOT_FOUND
                              .withDescription("Car not found: " + id)
                              .asRuntimeException()));

      responseObserver.onNext(dto);
      responseObserver.onCompleted();
      log.info("gRPC response: GetAvailableCarById id={}", dto.getId());
    } catch (IllegalArgumentException exception) {
      responseObserver.onError(
          Status.INVALID_ARGUMENT
              .withDescription("Invalid UUID")
              .withCause(exception)
              .asRuntimeException());
    } catch (RuntimeException exception) {
      if (Status.fromThrowable(exception).getCode() == Status.Code.NOT_FOUND) {
        responseObserver.onError(exception);
      } else {
        log.error("gRPC error in GetAvailableCarById", exception);
        responseObserver.onError(
            Status.INTERNAL
                .withDescription(exception.getMessage())
                .withCause(exception)
                .asRuntimeException());
      }
    }
  }

  private CarDto toDto(CarInStockEntity entity) {
    return CarDto.newBuilder()
        .setId(entity.getId().toString())
        .setModelId(entity.getModel().getId().toString())
        .setModelDisplayName(entity.getModel().displayName())
        .setColor(entity.getColor())
        .setPrice(entity.getPrice().toPlainString())
        .setAvailableForTestDrive(entity.isAvailableForTestDrive())
        .setCreatedAt(entity.getCreatedAt().toString())
        .setUpdatedAt(entity.getUpdatedAt().toString())
        .setRemoved(entity.isRemoved())
        .build();
  }
}
