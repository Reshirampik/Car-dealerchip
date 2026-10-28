package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "test_drive_requests")
public class TestDriveRequestEntity extends BaseEntity {
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "client_id", nullable = false)
  private UserEntity client;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "car_id", nullable = false)
  private CarInStockEntity car;

  @Column(name = "start_date_time", nullable = false)
  private Instant startDateTime;

  public TestDriveRequestEntity() {}

  public UserEntity getClient() {
    return client;
  }

  public void setClient(UserEntity client) {
    this.client = client;
  }

  public CarInStockEntity getCar() {
    return car;
  }

  public void setCar(CarInStockEntity car) {
    this.car = car;
  }

  public Instant getStartDateTime() {
    return startDateTime;
  }

  public void setStartDateTime(Instant startDateTime) {
    this.startDateTime = startDateTime;
  }
}
