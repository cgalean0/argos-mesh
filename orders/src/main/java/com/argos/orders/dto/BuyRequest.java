package com.argos.orders.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record BuyRequest(
    @NotNull Long userId,
    @NotNull Long productId,
    @PositiveOrZero Integer quantity
) {}
