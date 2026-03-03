package com.argos.orders.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// This class contain the information to create a product.
public record ProductRequest(
    @NotNull String productName,
    @Positive BigDecimal productPrice,
    @PositiveOrZero Integer productStock
) {}
