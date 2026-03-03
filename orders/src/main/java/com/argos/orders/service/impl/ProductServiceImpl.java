package com.argos.orders.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.argos.orders.dto.BuyRequest;
import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.ProductResponse;
import com.argos.orders.exceptions.ProductNotFoundException;
import com.argos.orders.mappers.ProductMapper;
import com.argos.orders.model.Product;
import com.argos.orders.repository.ProductRepository;
import com.argos.orders.service.IProductService;

@Service
public class ProductServiceImpl implements IProductService{

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper) {this.productRepository = productRepository; this.productMapper = productMapper;}

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
    public ProductResponse createProduct(ProductRequest prod) {
        Product createdProduct = new Product();
        productMapper.updateEntityFromDto(prod, createdProduct);
        Product savedProduct = productRepository.save(createdProduct);
        return productMapper.toDTO(savedProduct);
    }

    @Override
    public Boolean sellProduct(BuyRequest req) {
        int sellingProducts = productRepository.decrementStock(req.productId(), req.quantity());
        return sellingProducts > 0;
    }
    
}
