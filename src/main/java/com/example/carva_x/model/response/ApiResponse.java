package com.example.carva_x.model.response;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Boolean result;
    private T data;
}