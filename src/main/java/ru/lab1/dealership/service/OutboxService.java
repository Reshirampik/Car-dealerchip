package ru.lab1.dealership.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.domain.order.OutboxEventStatus;
import ru.lab1.dealership.messaging.event.OrderDecisionEvent;
import ru.lab1.dealership.messaging.event.OrderSentForApprovalEvent;
import ru.lab1.dealership.observability.TraceContext;
import ru.lab1.dealership.persistence.entity.OutboxEventEntity;
import ru.lab1.dealership.persistence.repository.OutboxEventRepository;

@Service
@Transactional
public class OutboxService {
  private static final Logger log = LoggerFactory.getLogger(OutboxService.class);

  private final OutboxEventRepository outboxEventRepository;
  private final ObjectMapper objectMapper;
  private final String orderSentRoutingKey;
  private final String orderApprovedRoutingKey;
  private final String orderRejectedRoutingKey;

  public OutboxService(
      OutboxEventRepository outboxEventRepository,
      ObjectMapper objectMapper,
      @Value("${app.rabbitmq.order-sent-routing-key}") String orderSentRoutingKey,
      @Value("${app.rabbitmq.order-approved-routing-key}") String orderApprovedRoutingKey,
      @Value("${app.rabbitmq.order-rejected-routing-key}") String orderRejectedRoutingKey) {
    this.outboxEventRepository =
        Objects.requireNonNull(outboxEventRepository, "outboxEventRepository");
    this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper");
    this.orderSentRoutingKey = orderSentRoutingKey;
    this.orderApprovedRoutingKey = orderApprovedRoutingKey;
    this.orderRejectedRoutingKey = orderRejectedRoutingKey;
  }

  public void enqueueOrderSentForApproval(OrderSentForApprovalEvent event) {
    save(
        "ORDER",
        event.orderId().toString(),
        orderSentRoutingKey,
        event.getClass().getSimpleName(),
        event,
        event.traceId());
  }

  public void enqueueOrderDecision(OrderDecisionEvent event) {
    String routingKey = event.approved() ? orderApprovedRoutingKey : orderRejectedRoutingKey;
    save(
        "ORDER",
        event.orderId().toString(),
        routingKey,
        event.getClass().getSimpleName(),
        event,
        event.traceId());
  }

  public void enqueue(
      String aggregateType,
      String aggregateId,
      String routingKey,
      String eventType,
      Object payload,
      String traceId) {
    save(aggregateType, aggregateId, routingKey, eventType, payload, traceId);
  }

  private void save(
      String aggregateType,
      String aggregateId,
      String routingKey,
      String eventType,
      Object payload,
      String traceId) {
    try {
      OutboxEventEntity entity = new OutboxEventEntity();
      entity.setAggregateType(aggregateType);
      entity.setAggregateId(aggregateId);
      entity.setRoutingKey(routingKey);
      entity.setEventType(eventType);
      entity.setPayload(objectMapper.writeValueAsString(payload));
      entity.setTraceId(TraceContext.normalize(traceId));
      entity.setStatus(OutboxEventStatus.NEW);
      outboxEventRepository.save(entity);

      log.info(
          "Queued outbox event type={} aggregateId={} routingKey={}",
          eventType,
          aggregateId,
          routingKey);
    } catch (Exception exception) {
      throw new IllegalStateException("Failed to serialize outbox event", exception);
    }
  }

  public String defaultTraceId() {
    return TraceContext.currentOrNew();
  }

  public Instant now() {
    return Instant.now();
  }
}
