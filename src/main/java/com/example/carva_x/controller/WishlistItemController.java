package com.example.carva_x.controller;

import com.example.carva_x.model.response.ApiResponse;
import com.example.carva_x.model.response.WishlistProductResponse;
import com.example.carva_x.service.WishlistService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/wishlist")
public class WishlistItemController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistProductResponse>>> getWishlist(
            @RequestHeader(value = "Authorization") String authHeader) {

        return ResponseEntity.ok(ApiResponse.<List<WishlistProductResponse>>builder()
                .result(true)
                .data(wishlistService.getWishlist(authHeader))
                .build());
    }

    @PostMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<WishlistProductResponse>> addProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId) {

        return ResponseEntity.ok(ApiResponse.<WishlistProductResponse>builder()
                .result(true)
                .data(wishlistService.addProduct(authHeader, productId))
                .build());
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId) {

        wishlistService.removeProduct(authHeader, productId);

        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(true)
                .build());
    }

    @PutMapping("/products/{productId}/replace/{replacementProductId}")
    public ResponseEntity<ApiResponse<WishlistProductResponse>> replaceProduct(
            @RequestHeader(value = "Authorization") String authHeader,
            @PathVariable Integer productId,
            @PathVariable Integer replacementProductId) {

        return ResponseEntity.ok(ApiResponse.<WishlistProductResponse>builder()
                .result(true)
                .data(wishlistService.replaceProduct(authHeader, productId, replacementProductId))
                .build());
    }
}
