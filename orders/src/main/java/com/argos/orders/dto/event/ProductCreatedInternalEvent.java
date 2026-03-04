package com.argos.orders.dto.event;

import com.argos.orders.dto.ProductResponse;

public record ProductCreatedInternalEvent(ProductResponse response) {}