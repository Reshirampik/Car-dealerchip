package ru.lab1.dealership.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
@EnableConfigurationProperties(RabbitMqProperties.class)
public class RabbitMqConfig {

  @Bean
  public DirectExchange dealershipExchange(RabbitMqProperties properties) {
    return new DirectExchange(properties.getExchange(), true, false);
  }

  @Bean
  public Queue orderSentQueue(RabbitMqProperties properties) {
    return new Queue(properties.getQueues().getOrderSent(), true);
  }

  @Bean
  public Queue orderApprovedQueue(RabbitMqProperties properties) {
    return new Queue(properties.getQueues().getOrderApproved(), true);
  }

  @Bean
  public Queue orderRejectedQueue(RabbitMqProperties properties) {
    return new Queue(properties.getQueues().getOrderRejected(), true);
  }

  @Bean
  public Binding orderSentBinding(
      DirectExchange dealershipExchange, Queue orderSentQueue, RabbitMqProperties properties) {
    return BindingBuilder.bind(orderSentQueue)
        .to(dealershipExchange)
        .with(properties.getOrderSentRoutingKey());
  }

  @Bean
  public Binding orderApprovedBinding(
      DirectExchange dealershipExchange, Queue orderApprovedQueue, RabbitMqProperties properties) {
    return BindingBuilder.bind(orderApprovedQueue)
        .to(dealershipExchange)
        .with(properties.getOrderApprovedRoutingKey());
  }

  @Bean
  public Binding orderRejectedBinding(
      DirectExchange dealershipExchange, Queue orderRejectedQueue, RabbitMqProperties properties) {
    return BindingBuilder.bind(orderRejectedQueue)
        .to(dealershipExchange)
        .with(properties.getOrderRejectedRoutingKey());
  }

  @Bean
  public MessageConverter jacksonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jacksonMessageConverter);
    return template;
  }

  @Bean
  public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory, MessageConverter jacksonMessageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jacksonMessageConverter);
    return factory;
  }
}
