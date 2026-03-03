package com.argos.orders.dto;

import java.math.BigDecimal;

// Record that show a product information.
public record ProductResponse(
    Long productID,
    String productName,
    BigDecimal productPrice,
    Integer productStock
) {}
