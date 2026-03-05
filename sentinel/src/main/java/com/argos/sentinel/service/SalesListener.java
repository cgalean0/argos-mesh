package com.argos.sentinel.service;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.argos.sentinel.config.RabbitMQConfig;
import com.argos.sentinel.dto.AlertInternalEvent;
import com.argos.sentinel.dto.ProductSoldInternalEvent;

@Component
public class SalesListener {
    private final TrafficAnalyzer analyzer;
    private final RabbitTemplate rabbitTemplate;
    private final RedisService redisService;

    public SalesListener(TrafficAnalyzer analyzer, RabbitTemplate rabbitTemplate, RedisService redisService) {
        this.analyzer = analyzer;
        this.rabbitTemplate = rabbitTemplate;
        this.redisService = redisService;
    }
    
    @RabbitListener(queues = "argos.sales.queue")
    public void processSalesEvents(ProductSoldInternalEvent data) {
        String ip = data.ipAddress();

        if (redisService.isBanned(ip)) {
            return;
        }

        if (analyzer.processAndCheckLimit(ip)) {
            AlertInternalEvent event = new AlertInternalEvent("Suspicious behavior", ip, "CRITICAL", LocalDateTime.now());            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                "argos.alert.security",
                event
            );
        } else {
            System.out.println("[ Sentinel🛡️ ] Normal traffic of the IP: " + ip);
        }
    }
}
