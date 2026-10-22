package com.example.carva_x.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class LoginResponse {
    private String accessToken;
}
