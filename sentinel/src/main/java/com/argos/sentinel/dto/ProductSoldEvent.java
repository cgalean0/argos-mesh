package com.argos.sentinel.dto;

import java.time.LocalDateTime;

public record ProductSoldEvent(
    Long productID,
    Integer quantity,
    String ipAddress,
    LocalDateTime timeStamp
) {}
