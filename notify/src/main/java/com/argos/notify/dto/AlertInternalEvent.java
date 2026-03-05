package com.argos.notify.dto;

import java.time.LocalDateTime;

public record AlertInternalEvent(
    String type, // DDOs Attack
    String sourceIp, // 127.0.0.1
    String severity, // CRITICAL
    LocalDateTime timeStamp
) {}
