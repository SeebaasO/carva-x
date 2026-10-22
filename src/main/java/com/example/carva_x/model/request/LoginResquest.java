package com.example.carva_x.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResquest {
    @NotBlank
    private  String email;
    @NotBlank
    private String password;
}
