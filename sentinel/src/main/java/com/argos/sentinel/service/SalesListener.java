package com.argos.sentinel.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.argos.sentinel.dto.ProductSoldInternalEvent;

@Component
public class SalesListener {
    private final TrafficAnalyzer analyzer;

    public SalesListener(TrafficAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @RabbitListener(queues = "argos.sales.queue", ackMode = "AUTO")
    public void processSalesEvents(ProductSoldInternalEvent data) {
        String ip = data.ipAddress();

        if (analyzer.processAndCheckLimit(ip)) {
            System.err.println("[ Sentinel🛡️ ] The ip: " + ip + " Exceded limit of peticions. I blocked it IP");
            // Here save the IP in a database such as Redis for block this and notify in the notifications services "argos-notify".
        } else {
            System.out.println("[ Sentinel🛡️ ] Normal traffic of the IP: " + ip);
        }
    }
}
