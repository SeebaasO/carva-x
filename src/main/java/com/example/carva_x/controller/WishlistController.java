package com.example.carva_x.controller;

import com.example.carva_x.model.request.ReplaceWishlistProductRequest;
import com.example.carva_x.model.request.WishlistProductRequest;
import com.example.carva_x.model.response.ApiResponse;
import com.example.carva_x.model.response.WishlistProductResponse;
import com.example.carva_x.service.WishlistService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WishlistProductResponse>>> getWishlist(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(ApiResponse.<List<WishlistProductResponse>>builder()
                .result(true)
                .data(wishlistService.getWishlist(userId))
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistProductResponse>> addProduct(
            @PathVariable Integer userId,
            @Valid @RequestBody WishlistProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<WishlistProductResponse>builder()
                        .result(true)
                        .data(wishlistService.addProduct(userId, request.getProductId()))
                        .build());
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId) {
        wishlistService.removeProduct(userId, productId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .result(true)
                .build());
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<WishlistProductResponse>> replaceProduct(
            @PathVariable Integer userId,
            @PathVariable Integer productId,
            @Valid @RequestBody ReplaceWishlistProductRequest request) {
        return ResponseEntity.ok(ApiResponse.<WishlistProductResponse>builder()
                .result(true)
                .data(wishlistService.replaceProduct(userId, productId, request.getReplacementProductId()))
                .build());
    }
}
