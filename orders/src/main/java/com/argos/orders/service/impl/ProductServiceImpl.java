package com.argos.orders.service.impl;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.ProductResponse;
import com.argos.orders.dto.event.ProductCreatedInternalEvent;
import com.argos.orders.dto.event.ProductSoldInternalEvent;
import com.argos.orders.exceptions.NotSuficientStockException;
import com.argos.orders.exceptions.ProductNotFoundException;
import com.argos.orders.mappers.ProductMapper;
import com.argos.orders.model.Product;
import com.argos.orders.repository.ProductRepository;
import com.argos.orders.service.IProductService;

@Service
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final ProductMapper productMapper;
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:ip:";

    public ProductServiceImpl(ProductRepository productRepository, ApplicationEventPublisher eventPublisher,
            ProductMapper productMapper, StringRedisTemplate redisTemplate) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.productMapper = productMapper;
        this.redisTemplate = redisTemplate;
    }
    

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product prod = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("ProductNotFound"));
        return productMapper.toDTO(prod);
    }


    @Override
    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id))
            throw new ProductNotFoundException("The product does not exits.");
        productRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest prod) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("ProductNotFound"));

        productMapper.updateEntityFromDto(prod, product);
        productRepository.save(product);
        return productMapper.toDTO(product);
    }

    @Override
    @Transactional
    public ProductResponse createProduct(ProductRequest prod) {
        Product createdProduct = new Product();
        productMapper.updateEntityFromDto(prod, createdProduct);
        Product savedProduct = productRepository.save(createdProduct);
        ProductResponse event = productMapper.toDTO(savedProduct);
        // We made sure that the message only send to Rabbit if the transaction finish correctly.
        eventPublisher.publishEvent(new ProductCreatedInternalEvent(event));
        return event;
    }

    @Override
    @Transactional
    public void sellProduct(Long id, String ipAddress, Integer quantity) {
        if (Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + ipAddress))) {
            throw new SecurityException("Access Denied: Your IP is blocked for suspicious behavior");
        }
        Product product = productRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if (product.getProductStock() < quantity) 
            throw new NotSuficientStockException("The stock is not sufficient");
        product.setProductStock(product.getProductStock() - quantity);
        productRepository.save(product);
        eventPublisher.publishEvent(new ProductSoldInternalEvent(id, quantity, ipAddress, LocalDateTime.now()));
    }

}
