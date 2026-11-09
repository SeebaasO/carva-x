package com.example.carva_x.model.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class ProductResponse {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer amount;
    private String description;
    private BigDecimal tax;
}
