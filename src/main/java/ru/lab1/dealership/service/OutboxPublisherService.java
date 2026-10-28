package ru.lab1.dealership.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.order.OutboxEventStatus;
import ru.lab1.dealership.persistence.entity.OutboxEventEntity;
import ru.lab1.dealership.persistence.repository.OutboxEventRepository;

@Service
public class OutboxPublisherService {
  private final OutboxEventRepository outboxEventRepository;
  private final RabbitTemplate rabbitTemplate;
  private final String exchange;
  private final ObjectMapper objectMapper;

  public OutboxPublisherService(
      OutboxEventRepository outboxEventRepository,
      RabbitTemplate rabbitTemplate,
      ObjectMapper objectMapper,
      @Value("${app.rabbitmq.exchange}") String exchange) {
    this.outboxEventRepository =
        Objects.requireNonNull(outboxEventRepository, "outboxEventRepository");
    this.rabbitTemplate = Objects.requireNonNull(rabbitTemplate, "rabbitTemplate");
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    this.exchange = exchange;
  }

  @Scheduled(fixedDelayString = "${app.outbox.publish-interval-ms:5000}")
  @Transactional
  public void publishPendingEvents() {
    List<OutboxEventEntity> pending =
        outboxEventRepository.findAllByRemovedFalseAndStatus(OutboxEventStatus.NEW);
    for (OutboxEventEntity event : pending) {
      try {
        Object payload = deserialize(event.getEventType(), event.getPayload());
        rabbitTemplate.convertAndSend(exchange, event.getRoutingKey(), payload);
        event.setStatus(OutboxEventStatus.SENT);
        event.setPublishedAt(Instant.now());
        outboxEventRepository.save(event);
      } catch (Exception exception) {
        event.setStatus(OutboxEventStatus.FAILED);
        outboxEventRepository.save(event);
      }
    }
  }

  private Object deserialize(String eventType, String payload) {
    try {
      return switch (eventType) {
        case "OrderSentForApprovalEvent" ->
            objectMapper.readValue(
                payload, ru.lab1.dealership.messaging.event.OrderSentForApprovalEvent.class);
        case "OrderDecisionEvent" ->
            objectMapper.readValue(
                payload, ru.lab1.dealership.messaging.event.OrderDecisionEvent.class);
        default -> payload;
      };
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to deserialize outbox event " + eventType, exception);
    }
  }
}
