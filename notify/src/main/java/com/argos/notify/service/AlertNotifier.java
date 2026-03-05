package com.argos.notify.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.argos.notify.dto.AlertInternalEvent;

@Service
public class AlertNotifier {

    @RabbitListener(queues = "argos.alert.queue")
    public void sendNotification(AlertInternalEvent alert) {
        System.out.println("[ Alert ] - " + alert.timeStamp());
        System.out.println("The IP: " + alert.sourceIp());
        System.out.println("Is suspicious of try an: " + alert.type());
        System.out.println("This is: " + alert.severity());
    }
}
