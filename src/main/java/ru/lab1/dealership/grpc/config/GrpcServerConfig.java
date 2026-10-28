package ru.lab1.dealership.grpc.config;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import ru.lab1.dealership.grpc.service.CarStockGrpcService;

@Configuration
@Profile("storage-service")
public class GrpcServerConfig {

  @Bean(initMethod = "start", destroyMethod = "shutdown")
  public Server grpcServer(
      @Value("${grpc.server.port:9090}") int port, CarStockGrpcService carStockGrpcService) {
    return NettyServerBuilder.forPort(port).addService(carStockGrpcService).build();
  }
}
