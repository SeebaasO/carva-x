package com.example.carva_x.model.response;

import com.example.carva_x.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistProductResponse {

    private Product product;
    private boolean outOfStock;
    private String notification;
}
