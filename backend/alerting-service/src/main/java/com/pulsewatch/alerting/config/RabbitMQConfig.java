package com.pulsewatch.alerting.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${alerting.rabbitmq.queue.name:alerts.queue}")
    private String queueName;

    @Value("${alerting.rabbitmq.exchange:alerts.exchange}")
    private String exchangeName;

    @Value("${alerting.rabbitmq.routing-key:alerts.routing.key}")
    private String routingKey;

    @Bean
    public Queue alertsQueue() {
        return new Queue(queueName, true);
    }

    @Bean
    public TopicExchange alertsExchange() {
        return new TopicExchange(exchangeName);
    }

    @Bean
    public Binding binding(Queue alertsQueue, TopicExchange alertsExchange) {
        return BindingBuilder.bind(alertsQueue)
                .to(alertsExchange)
                .with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
} 