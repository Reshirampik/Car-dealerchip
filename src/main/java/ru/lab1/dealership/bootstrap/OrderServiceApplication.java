package ru.lab1.dealership.bootstrap;

import org.springframework.boot.SpringApplication;

public final class OrderServiceApplication {
  private OrderServiceApplication() {}

  public static void main(String[] args) {
    SpringApplication application =
        new SpringApplication(ru.lab1.dealership.DealershipApplication.class);
    application.setAdditionalProfiles("order-service");
    application.run(args);
  }
}
