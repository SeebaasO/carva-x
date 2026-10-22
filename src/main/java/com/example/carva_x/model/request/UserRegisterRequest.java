package com.example.carva_x.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String addres;
    @NotBlank
    private String role;
}