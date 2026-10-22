package com.example.carva_x.service;

import com.example.carva_x.entity.User;
import com.example.carva_x.model.JwtValidate;

public interface JwtService {

    String generateJwtToken(User user);

    JwtValidate validateAccessToken (String authHeader);

}
