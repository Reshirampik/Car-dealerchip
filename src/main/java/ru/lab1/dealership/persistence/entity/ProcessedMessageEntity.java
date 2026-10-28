package ru.lab1.dealership.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_messages")
public class ProcessedMessageEntity extends BaseEntity {
  @Column(name = "consumer_name", nullable = false)
  private String consumerName;

  @Column(name = "message_id", nullable = false)
  private UUID messageId;

  @Column(name = "processed_at", nullable = false)
  private Instant processedAt = Instant.now();

  public String getConsumerName() {
    return consumerName;
  }

  public void setConsumerName(String consumerName) {
    this.consumerName = consumerName;
  }

  public UUID getMessageId() {
    return messageId;
  }

  public void setMessageId(UUID messageId) {
    this.messageId = messageId;
  }

  public Instant getProcessedAt() {
    return processedAt;
  }

  public void setProcessedAt(Instant processedAt) {
    this.processedAt = processedAt;
  }
}
