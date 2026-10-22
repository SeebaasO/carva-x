package com.example.carva_x.exception;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int statusCode;
}

