package com.example.carva_x.service;

import com.example.carva_x.model.response.WishlistProductResponse;

import java.util.List;

public interface WishlistService {

    List<WishlistProductResponse> getWishlist(Integer userId);

    WishlistProductResponse addProduct(Integer userId, Integer productId);

    void removeProduct(Integer userId, Integer productId);

    WishlistProductResponse replaceProduct(Integer userId, Integer productId, Integer replacementProductId);
}
