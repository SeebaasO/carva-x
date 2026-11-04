package com.example.carva_x.model.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WishlistProductResponse {

    private ProductResponse product;
    private boolean outOfStock;
    private String notification;
}
