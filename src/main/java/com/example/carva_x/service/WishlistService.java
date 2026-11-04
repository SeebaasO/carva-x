package com.example.carva_x.service;

import com.example.carva_x.model.response.WishlistProductResponse;

import java.util.List;

public interface WishlistService {

    List<WishlistProductResponse> getWishlist(String authHeader);

    WishlistProductResponse addProduct(String authHeader, Integer productId);

    void removeProduct(String authHeader, Integer productId);

    WishlistProductResponse replaceProduct(String authHeader, Integer productId, Integer replacementProductId);
}
