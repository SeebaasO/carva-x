package com.example.carva_x.service;

import com.example.carva_x.model.request.LoginResquest;
import com.example.carva_x.model.request.UserRegisterRequest;
import com.example.carva_x.model.response.LoginResponse;

public interface AuthService {

    LoginResponse registerUser(UserRegisterRequest userRegisterRequest);

    LoginResponse login(LoginResquest loginResquest);
}