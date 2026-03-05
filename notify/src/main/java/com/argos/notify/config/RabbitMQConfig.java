package com.argos.notify.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_ALERT = "argos.alert.queue";
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String RK_ALERT = "argos.alert.#";

    @Bean
    public Queue alertQueue() {
        return new Queue(QUEUE_ALERT, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(ALERT_EXCHANGE);
    }

    @Bean
    public Binding binding(Queue alertQueue, TopicExchange exchange) {
        return BindingBuilder.bind(alertQueue).to(exchange).with(RK_ALERT);
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
