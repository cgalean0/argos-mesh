package com.argos.orders.service.impl;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.argos.orders.config.RabbitMQConfig;
import com.argos.orders.dto.event.ProductCreatedInternalEvent;
import com.argos.orders.dto.event.ProductSoldInternalEvent;

@Component
public class ProductMessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public ProductMessagePublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductCreated(ProductCreatedInternalEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.RK_PRODUCTS,
                event.response());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductSold(ProductSoldInternalEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.RK_SALES,
                event);
    }
}