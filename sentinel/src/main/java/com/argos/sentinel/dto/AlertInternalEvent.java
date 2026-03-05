package com.argos.sentinel.dto;

import java.time.LocalDateTime;

public record AlertInternalEvent(
    String type,
    String sourceIp,
    String severity,
    LocalDateTime timeStamp
) {}
