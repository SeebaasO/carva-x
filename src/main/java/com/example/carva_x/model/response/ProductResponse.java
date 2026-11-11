package com.example.carva_x.model.response;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Integer id;
    private String name;
    private Double price;
    private Integer amount;
    private String description;
    private Double tax;
}