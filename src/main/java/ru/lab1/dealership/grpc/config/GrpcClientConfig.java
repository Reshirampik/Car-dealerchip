package ru.lab1.dealership.grpc.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.lab1.dealership.grpc.CarStockServiceGrpc;

@Configuration
@Profile("order-service")
public class GrpcClientConfig {

  @Bean(destroyMethod = "shutdownNow")
  public ManagedChannel storageChannel(
      @Value("${app.grpc.storage-target:localhost:9090}") String target) {
    return ManagedChannelBuilder.forTarget(target).usePlaintext().build();
  }

  @Bean
  public CarStockServiceGrpc.CarStockServiceBlockingStub carStockBlockingStub(
      ManagedChannel storageChannel) {
    return CarStockServiceGrpc.newBlockingStub(storageChannel);
  }
}
