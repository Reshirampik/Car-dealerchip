package ru.lab1.dealership.messaging.listener;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.lab1.dealership.messaging.event.OrderDecisionEvent;
import ru.lab1.dealership.messaging.event.OrderSentForApprovalEvent;
import ru.lab1.dealership.messaging.event.OrderType;
import ru.lab1.dealership.observability.TraceContext;
import ru.lab1.dealership.persistence.entity.ProcessedMessageEntity;
import ru.lab1.dealership.persistence.repository.ProcessedMessageRepository;
import ru.lab1.dealership.service.AssemblyOrderService;
import ru.lab1.dealership.service.OutboxService;

@Profile("storage-service & !test")
@Component
public class StorageOrderListener {
  private static final Logger log = LoggerFactory.getLogger(StorageOrderListener.class);
  private static final String CONSUMER_NAME = "storage-order-listener";

  private final AssemblyOrderService assemblyOrderService;
  private final OutboxService outboxService;
  private final ProcessedMessageRepository processedMessageRepository;

  public StorageOrderListener(
      AssemblyOrderService assemblyOrderService,
      OutboxService outboxService,
      ProcessedMessageRepository processedMessageRepository) {
    this.assemblyOrderService =
        Objects.requireNonNull(assemblyOrderService, "assemblyOrderService");
    this.outboxService = Objects.requireNonNull(outboxService, "outboxService");
    this.processedMessageRepository =
        Objects.requireNonNull(processedMessageRepository, "processedMessageRepository");
  }

  @RabbitListener(queues = "${app.rabbitmq.queues.order-sent}")
  @Transactional
  public void handle(OrderSentForApprovalEvent event) {
    withTrace(
        event.traceId(),
        () -> {
          if (processedMessageRepository
              .findByConsumerNameAndMessageIdAndRemovedFalse(CONSUMER_NAME, event.eventId())
              .isPresent()) {
            log.info(
                "Skipping duplicated order-sent event eventId={} orderId={}",
                event.eventId(),
                event.orderId());
            return;
          }

          boolean approved =
              event.orderType() == OrderType.COMMON
                  ? event.carId() != null
                  : event.modelId() != null
                      && event.componentIds() != null
                      && !event.componentIds().isEmpty();

          log.info(
              "Processing order-sent event eventId={} orderId={} approved={}",
              event.eventId(),
              event.orderId(),
              approved);

          if (approved) {
            assemblyOrderService.create(
                new ru.lab1.dealership.web.dto.AssemblyOrderRequest(
                    event.orderId(),
                    event.orderType().name(),
                    event.carId(),
                    event.modelId(),
                    event.componentIds(),
                    null,
                    "ASSEMBLED"));
          } else {
            assemblyOrderService.create(
                new ru.lab1.dealership.web.dto.AssemblyOrderRequest(
                    event.orderId(),
                    event.orderType().name(),
                    event.carId(),
                    event.modelId(),
                    event.componentIds(),
                    null,
                    "FAIL"));
          }

          outboxService.enqueueOrderDecision(
              new OrderDecisionEvent(
                  event.eventId(),
                  event.orderId(),
                  event.orderType(),
                  approved,
                  event.traceId(),
                  approved ? "assembled" : "failed",
                  event.createdAt()));

          ProcessedMessageEntity processed = new ProcessedMessageEntity();
          processed.setConsumerName(CONSUMER_NAME);
          processed.setMessageId(event.eventId());
          processedMessageRepository.save(processed);
        });
  }

  private void withTrace(String traceId, Runnable action) {
    String previous = MDC.get(TraceContext.TRACE_ID_KEY);
    try {
      MDC.put(TraceContext.TRACE_ID_KEY, TraceContext.normalize(traceId));
      action.run();
    } finally {
      if (previous == null || previous.isBlank()) {
        MDC.remove(TraceContext.TRACE_ID_KEY);
      } else {
        MDC.put(TraceContext.TRACE_ID_KEY, previous);
      }
    }
  }
}
