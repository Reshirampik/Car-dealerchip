package ru.lab1.dealership.bootstrap;

import org.springframework.boot.SpringApplication;

public final class StorageServiceApplication {
  private StorageServiceApplication() {}

  public static void main(String[] args) {
    SpringApplication application =
        new SpringApplication(ru.lab1.dealership.DealershipApplication.class);
    application.setAdditionalProfiles("storage-service");
    application.run(args);
  }
}
