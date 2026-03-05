package com.argos.sentinel;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_ALERT = "argos.alert.queue";
    public static final String ALERT_EXCHANGE = "alert.exchange";
    public static final String RK_ALERT = "argos.alert.#";

    public static final String QUEUE_SALE = "argos.sales.queue";    
    public static final String EXCHANGE_SOLD = "shop.exchange";
    public static final String RK_SALES = "shop.event.sold";

    /*
    * Notifications queue
    */
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

    /**
     * Sales queue
     */
    @Bean
    public Queue salesQueue() {
        return new Queue(QUEUE_SALE, true);
    }

    @Bean
    public TopicExchange exchangeSold() {
        return new TopicExchange(EXCHANGE_SOLD);
    }

    @Bean
    public Binding bindingSales(Queue salesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(salesQueue).to(exchange).with(RK_SALES);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }


    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
