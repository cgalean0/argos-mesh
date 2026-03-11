package com.argos.orders;

import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.event.ProductCreatedInternalEvent;
import com.argos.orders.dto.event.ProductSoldInternalEvent;
import com.argos.orders.exceptions.NotSuficientStockException;
import com.argos.orders.exceptions.ProductNotFoundException;
import com.argos.orders.mappers.ProductMapper;
import com.argos.orders.model.Product;
import com.argos.orders.repository.ProductRepository;
import com.argos.orders.service.impl.ProductServiceImpl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    @Mock
    ProductRepository repository;
    @Mock
    StringRedisTemplate redisTemplate;
    @Mock
    ProductMapper mapper;
    @Mock
    ApplicationEventPublisher eventPublisher;
    @InjectMocks
    ProductServiceImpl service;

    @Test
    @DisplayName("Should be thrown InsufficientStockException when the stock of product are not sufficient")
    void ShouldThrowWhenStockIsInsufficient() {
        Long id = 1L;
        Product prod = new Product();
        prod.setProductID(id);
        prod.setProductStock(100);
        prod.setProductName("Test"); //Only Because is NotNull
        prod.setProductPrice(BigDecimal.valueOf(100));
        String ip = "127.0.0.1";
        // Indicate that th ip is not blocked
        when(redisTemplate.hasKey("blacklist:ip:" + ip)).thenReturn(false);

        when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(prod));

        assertThrows(NotSuficientStockException.class, () -> {
           service.sellProduct(id, ip, 101);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void ShouldBeTrueIfStockIsSufficient() {
        Long id = 1L;
        Product prod = new Product();
        prod.setProductID(id);
        prod.setProductStock(100);
        prod.setProductName("Test"); //Only Because is NotNull
        prod.setProductPrice(BigDecimal.valueOf(100));
        String ip = "127.0.0.1";
        // Indicate that th ip is not blocked
        when(redisTemplate.hasKey("blacklist:ip:" + ip)).thenReturn(false);
        when(repository.findByIdForUpdate(id)).thenReturn(Optional.of(prod));
        // Act
        service.sellProduct(id, ip, 100);
        // Verifications
        verify(repository, times(1)).save(argThat(p -> p.getProductStock() == 0));
        verify(eventPublisher, times(1)).publishEvent(any(ProductSoldInternalEvent.class));
    }

    @Test
    void productIsCreated() {
        ProductRequest prod = new ProductRequest("TestSaved", BigDecimal.valueOf(100), 1);
        service.createProduct(prod);
        verify(repository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishEvent(any(ProductCreatedInternalEvent.class));
    }

    @Test
    void ShouldThrowProductNotFoundExceptionWhenIWantUpdateProduct() {
        Long id = 1L;
        ProductRequest prod = new ProductRequest("Test", BigDecimal.valueOf(100), 2);
        assertThrows(ProductNotFoundException.class, () -> {
            service.updateProduct(id, prod);
        });
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any(ProductCreatedInternalEvent.class));
    }

    @Test
    void ShouldUpdateTheProduct() {
        Long id = 1L;
        ProductRequest prod = new ProductRequest("Test", BigDecimal.valueOf(100), 2);
        Product mockProd = new Product(id, "Test", BigDecimal.valueOf(50), 1);
        when(repository.findById(id)).thenReturn(Optional.of(mockProd));

        service.updateProduct(id, prod);

        verify(repository, times(1)).save(argThat(p -> p.getProductID() == 1L));
    }
}
