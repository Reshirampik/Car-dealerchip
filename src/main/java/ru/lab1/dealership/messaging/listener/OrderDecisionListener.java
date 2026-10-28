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
import ru.lab1.dealership.messaging.event.OrderType;
import ru.lab1.dealership.observability.TraceContext;
import ru.lab1.dealership.persistence.entity.ProcessedMessageEntity;
import ru.lab1.dealership.persistence.repository.ProcessedMessageRepository;
import ru.lab1.dealership.service.CommonOrderService;
import ru.lab1.dealership.service.CustomOrderService;

@Component
@Profile("order-service & !test")
public class OrderDecisionListener {

  private static final Logger log = LoggerFactory.getLogger(OrderDecisionListener.class);

  private static final String CONSUMER_NAME = "order-decision-listener";

  private final CommonOrderService commonOrderService;
  private final CustomOrderService customOrderService;
  private final ProcessedMessageRepository processedMessageRepository;

  public OrderDecisionListener(
      CommonOrderService commonOrderService,
      CustomOrderService customOrderService,
      ProcessedMessageRepository processedMessageRepository) {

    this.commonOrderService = Objects.requireNonNull(commonOrderService);

    this.customOrderService = Objects.requireNonNull(customOrderService);

    this.processedMessageRepository = Objects.requireNonNull(processedMessageRepository);
  }

  @RabbitListener(queues = "${app.rabbitmq.queues.order-approved}")
  @Transactional
  public void handleApproved(OrderDecisionEvent event) {
    if (event.approved()) {
      handle(event);
    }
  }

  @RabbitListener(queues = "${app.rabbitmq.queues.order-rejected}")
  @Transactional
  public void handleRejected(OrderDecisionEvent event) {
    if (!event.approved()) {
      handle(event);
    }
  }

  private void handle(OrderDecisionEvent event) {

    withTrace(
        event.traceId(),
        () -> {
          if (isProcessed(event)) {
            log.info(
                "Skipping duplicated order-decision event eventId={} orderId={}",
                event.eventId(),
                event.orderId());

            return;
          }

          if (event.orderType() == OrderType.COMMON) {

            if (event.approved()) {
              commonOrderService.readyForPickup(event.orderId());
            } else {
              commonOrderService.cancel(event.orderId());
            }

          } else {

            if (event.approved()) {
              customOrderService.waitDelivery(event.orderId());
              customOrderService.readyForPickup(event.orderId());
            } else {
              customOrderService.cancel(event.orderId());
            }
          }

          log.info(
              "Processed order-decision event eventId={} orderId={} approved={}",
              event.eventId(),
              event.orderId(),
              event.approved());

          markProcessed(event);
        });
  }

  private boolean isProcessed(OrderDecisionEvent event) {

    return processedMessageRepository
        .findByConsumerNameAndMessageIdAndRemovedFalse(CONSUMER_NAME, event.eventId())
        .isPresent();
  }

  private void markProcessed(OrderDecisionEvent event) {

    ProcessedMessageEntity processed = new ProcessedMessageEntity();

    processed.setConsumerName(CONSUMER_NAME);
    processed.setMessageId(event.eventId());

    processedMessageRepository.save(processed);
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
