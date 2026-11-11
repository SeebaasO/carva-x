package com.example.carva_x.service;

import com.example.carva_x.model.request.ProductRequest;
import com.example.carva_x.model.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts(String authHeader);

    ProductResponse getProduct(String authHeader, Integer productId);

    ProductResponse createProduct(String authHeader, ProductRequest productRequest);

    ProductResponse updateProduct(String authHeader, Integer productId, ProductRequest productRequest);

    void deleteProduct(String authHeader, Integer productId);
}
