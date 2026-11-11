package com.example.carva_x.controller;

import com.example.carva_x.model.request.ProductRequest;
import com.example.carva_x.model.response.ApiResponse;
import com.example.carva_x.model.response.ProductResponse;
import com.example.carva_x.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProducts(
            @RequestHeader(value = "Authorization") String authHeader) {
        return ResponseEntity.ok(ApiResponse.<List<ProductResponse>>builder()
                .result(true)
                .data(productService.getProducts(authHeader))
                .build());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId) {
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .result(true)
                .data(productService.getProduct(authHeader, productId))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductResponse>builder()
                        .result(true)
                        .data(productService.createProduct(authHeader, productRequest))
                        .build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId,
            @Valid @RequestBody ProductRequest productRequest) {
        return ResponseEntity.ok(ApiResponse.<ProductResponse>builder()
                .result(true)
                .data(productService.updateProduct(authHeader, productId, productRequest))
                .build());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId) {
        productService.deleteProduct(authHeader, productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(true)
                .build());
    }
}
