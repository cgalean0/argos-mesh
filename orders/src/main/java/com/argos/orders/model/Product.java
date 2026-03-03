package com.argos.orders.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table (name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @Column (name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productID;
    @NotNull
    @NotBlank
    @Column (name = "product_name", nullable = false)
    private String productName;
    @NotNull
    @Positive
    @Column (name = "product_price", nullable = false)
    private BigDecimal productPrice;
    @NotNull
    @PositiveOrZero
    @Column (name = "product_stock")
    private Integer productStock;
}
