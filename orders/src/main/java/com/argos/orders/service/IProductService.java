package com.argos.orders.service;

import com.argos.orders.dto.BuyRequest;
import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.ProductResponse;

// Is the contract that offers at the controller
public interface IProductService {
    ProductResponse getProductById(Long id);
    void deleteProduct(Long id);
    ProductResponse updateProduct(Long id, ProductRequest prod);
    ProductResponse createProduct(ProductRequest prod);
    Boolean sellProduct(BuyRequest req);
}
