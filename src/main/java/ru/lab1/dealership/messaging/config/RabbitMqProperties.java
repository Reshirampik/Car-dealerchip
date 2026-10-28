package ru.lab1.dealership.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rabbitmq")
public class RabbitMqProperties {
  private String exchange;
  private String orderSentRoutingKey;
  private String orderApprovedRoutingKey;
  private String orderRejectedRoutingKey;
  private final Queues queues = new Queues();

  public String getExchange() {
    return exchange;
  }

  public void setExchange(String exchange) {
    this.exchange = exchange;
  }

  public String getOrderSentRoutingKey() {
    return orderSentRoutingKey;
  }

  public void setOrderSentRoutingKey(String orderSentRoutingKey) {
    this.orderSentRoutingKey = orderSentRoutingKey;
  }

  public String getOrderApprovedRoutingKey() {
    return orderApprovedRoutingKey;
  }

  public void setOrderApprovedRoutingKey(String orderApprovedRoutingKey) {
    this.orderApprovedRoutingKey = orderApprovedRoutingKey;
  }

  public String getOrderRejectedRoutingKey() {
    return orderRejectedRoutingKey;
  }

  public void setOrderRejectedRoutingKey(String orderRejectedRoutingKey) {
    this.orderRejectedRoutingKey = orderRejectedRoutingKey;
  }

  public Queues getQueues() {
    return queues;
  }

  public static class Queues {
    private String orderSent;
    private String orderApproved;
    private String orderRejected;

    public String getOrderSent() {
      return orderSent;
    }

    public void setOrderSent(String orderSent) {
      this.orderSent = orderSent;
    }

    public String getOrderApproved() {
      return orderApproved;
    }

    public void setOrderApproved(String orderApproved) {
      this.orderApproved = orderApproved;
    }

    public String getOrderRejected() {
      return orderRejected;
    }

    public void setOrderRejected(String orderRejected) {
      this.orderRejected = orderRejected;
    }
  }
}
