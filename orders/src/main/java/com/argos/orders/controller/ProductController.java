package com.argos.orders.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.argos.orders.dto.BuyRequest;
import com.argos.orders.dto.ProductRequest;
import com.argos.orders.dto.ProductResponse;
import com.argos.orders.service.IProductService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/orders/products")
public class ProductController {
    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductById(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest req) {
        return ResponseEntity.ok(productService.createProduct(req));
    }

    @PostMapping("/{id}/sell")
    public ResponseEntity<Void> sellProduct(
            @PathVariable Long id,
            @RequestBody BuyRequest buyRequest,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr(); // Extract the client IP.
        productService.sellProduct(id, clientIp, buyRequest.quantity());

        return ResponseEntity.accepted().build();
    }
}
