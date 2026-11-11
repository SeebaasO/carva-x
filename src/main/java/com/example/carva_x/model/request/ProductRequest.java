package com.example.carva_x.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.0")
    private Double price;

    @NotNull
    @PositiveOrZero
    private Integer amount;

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin("0.0")
    private Double tax;
}
