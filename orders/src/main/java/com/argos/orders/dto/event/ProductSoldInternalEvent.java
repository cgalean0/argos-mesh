package com.argos.orders.dto.event;

import java.time.LocalDateTime;

public record ProductSoldInternalEvent(
    Long productID,
    Integer quantity,
    String ipAddress,
    LocalDateTime timeStamp
) {}
