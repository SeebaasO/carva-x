package com.example.carva_x.service;

import com.example.carva_x.model.request.ProductRequest;
import com.example.carva_x.model.response.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> getProducts();

    ProductResponse getProduct(Integer productId);

    ProductResponse createProduct(ProductRequest productRequest);

    ProductResponse updateProduct(Integer productId, ProductRequest productRequest);

    void deleteProduct(Integer productId);
}
