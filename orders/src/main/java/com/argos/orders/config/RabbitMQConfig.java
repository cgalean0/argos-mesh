package com.argos.orders.config;

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

    public static final String QUEUE_SALE = "argos.sales.queue";
    public static final String QUEUE_PRODUCTS = "argos.products.mgmt.queue";
    
    public static final String EXCHANGE_NAME = "shop.exchange";
    public static final String RK_SALES = "shop.event.sold";
    public static final String RK_PRODUCTS = "shop.event.product.#";

    @Bean
    public Queue salesQueue() {
        return new Queue(QUEUE_SALE, true);
    }

    @Bean
    public Queue productsQueue() {
        return new Queue(QUEUE_PRODUCTS, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding bindingSales(Queue salesQueue, TopicExchange exchange) {
        return BindingBuilder.bind(salesQueue).to(exchange).with(RK_SALES);
    }

    @Bean
    public Binding bindingProducts(Queue productsQueue, TopicExchange exchange) {
        return BindingBuilder.bind(productsQueue).to(exchange).with(RK_PRODUCTS);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // Esto es clave para que tus Records con LocalDateTime no exploten
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
