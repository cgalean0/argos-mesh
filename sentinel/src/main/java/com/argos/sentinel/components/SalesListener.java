package com.argos.sentinel.components;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.argos.sentinel.dto.ProductSoldEvent;

@Component
public class SalesListener {
    private final TrafficAnalyzer analyzer;

    public SalesListener(TrafficAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @RabbitListener(queues = "argos.sales.queue")
    public void processSalesEvents(ProductSoldEvent data) {
        String ip = data.ipAddress();

        if (analyzer.shouldBlock(ip)) {
            System.err.println("[ Sentinel🛡️ ] The ip: " + ip + " Exceded limit of peticions. I blocked it IP");
            // Here save the IP in a database such as Redis for block this and notify in the notifications services "argos-notify".
        } else {
            System.out.println("[ Sentinel🛡️ ] Normal traffic of the IP: " + ip);
        }
    }
}
