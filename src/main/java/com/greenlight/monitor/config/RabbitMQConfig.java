package com.greenlight.monitor.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do RabbitMQ para recebimento de mensagens de sensores e envio de alertas.
 */
@Configuration
@EnableRabbit
public class RabbitMQConfig {

    // Configurações das filas e exchanges
    @Value("${app.rabbitmq.queue.sensor-readings:sensor.readings}")
    private String sensorReadingsQueue;

    @Value("${app.rabbitmq.queue.alerts:alerts}")
    private String alertsQueue;

    @Value("${app.rabbitmq.exchange.sensor:sensor.exchange}")
    private String sensorExchange;

    @Value("${app.rabbitmq.exchange.alerts:alerts.exchange}")
    private String alertsExchange;

    @Value("${app.rabbitmq.routing-key.sensor-readings:sensor.readings.key}")
    private String sensorReadingsRoutingKey;

    @Value("${app.rabbitmq.routing-key.alerts:alerts.key}")
    private String alertsRoutingKey;

    // Message Converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate configuration
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    // Listener Container Factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        return factory;
    }

    // ========== SENSOR EXCHANGE AND QUEUE ==========

    /**
     * Exchange para recebimento de dados de sensores
     */
    @Bean
    public TopicExchange sensorExchange() {
        return ExchangeBuilder
                .topicExchange(sensorExchange)
                .durable(true)
                .build();
    }

    /**
     * Fila para leituras de sensores
     */
    @Bean
    public Queue sensorReadingsQueue() {
        return QueueBuilder
                .durable(sensorReadingsQueue)
                .withArgument("x-dead-letter-exchange", sensorExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", "sensor.readings.failed")
                .withArgument("x-message-ttl", 3600000) // 1 hour TTL
                .build();
    }

    /**
     * Binding da fila de leituras de sensores
     */
    @Bean
    public Binding sensorReadingsBinding() {
        return BindingBuilder
                .bind(sensorReadingsQueue())
                .to(sensorExchange())
                .with(sensorReadingsRoutingKey);
    }

    /**
     * Dead Letter Exchange para mensagens de sensores que falharam
     */
    @Bean
    public TopicExchange sensorDeadLetterExchange() {
        return ExchangeBuilder
                .topicExchange(sensorExchange + ".dlx")
                .durable(true)
                .build();
    }

    /**
     * Dead Letter Queue para mensagens de sensores que falharam
     */
    @Bean
    public Queue sensorDeadLetterQueue() {
        return QueueBuilder
                .durable(sensorReadingsQueue + ".dlq")
                .build();
    }

    /**
     * Binding da Dead Letter Queue para sensores
     */
    @Bean
    public Binding sensorDeadLetterBinding() {
        return BindingBuilder
                .bind(sensorDeadLetterQueue())
                .to(sensorDeadLetterExchange())
                .with("sensor.readings.failed");
    }

    // ========== ALERTS EXCHANGE AND QUEUE ==========

    /**
     * Exchange para envio de alertas
     */
    @Bean
    public TopicExchange alertsExchange() {
        return ExchangeBuilder
                .topicExchange(alertsExchange)
                .durable(true)
                .build();
    }

    /**
     * Fila para alertas
     */
    @Bean
    public Queue alertsQueue() {
        return QueueBuilder
                .durable(alertsQueue)
                .withArgument("x-dead-letter-exchange", alertsExchange + ".dlx")
                .withArgument("x-dead-letter-routing-key", "alerts.failed")
                .build();
    }

    /**
     * Binding da fila de alertas
     */
    @Bean
    public Binding alertsBinding() {
        return BindingBuilder
                .bind(alertsQueue())
                .to(alertsExchange())
                .with(alertsRoutingKey);
    }

    /**
     * Dead Letter Exchange para alertas que falharam
     */
    @Bean
    public TopicExchange alertsDeadLetterExchange() {
        return ExchangeBuilder
                .topicExchange(alertsExchange + ".dlx")
                .durable(true)
                .build();
    }

    /**
     * Dead Letter Queue para alertas que falharam
     */
    @Bean
    public Queue alertsDeadLetterQueue() {
        return QueueBuilder
                .durable(alertsQueue + ".dlq")
                .build();
    }

    /**
     * Binding da Dead Letter Queue para alertas
     */
    @Bean
    public Binding alertsDeadLetterBinding() {
        return BindingBuilder
                .bind(alertsDeadLetterQueue())
                .to(alertsDeadLetterExchange())
                .with("alerts.failed");
    }

    // ========== ADDITIONAL QUEUES ==========

    /**
     * Fila para notificações por email
     */
    @Bean
    public Queue emailNotificationsQueue() {
        return QueueBuilder
                .durable("email.notifications")
                .build();
    }

    /**
     * Binding da fila de notificações por email
     */
    @Bean
    public Binding emailNotificationsBinding() {
        return BindingBuilder
                .bind(emailNotificationsQueue())
                .to(alertsExchange())
                .with("alerts.email");
    }

    /**
     * Fila para notificações SMS
     */
    @Bean
    public Queue smsNotificationsQueue() {
        return QueueBuilder
                .durable("sms.notifications")
                .build();
    }

    /**
     * Binding da fila de notificações SMS
     */
    @Bean
    public Binding smsNotificationsBinding() {
        return BindingBuilder
                .bind(smsNotificationsQueue())
                .to(alertsExchange())
                .with("alerts.sms");
    }

    /**
     * Fila para processamento de dados históricos
     */
    @Bean
    public Queue historicalDataQueue() {
        return QueueBuilder
                .durable("historical.data")
                .build();
    }

    /**
     * Binding da fila de dados históricos
     */
    @Bean
    public Binding historicalDataBinding() {
        return BindingBuilder
                .bind(historicalDataQueue())
                .to(sensorExchange())
                .with("sensor.historical");
    }
}

