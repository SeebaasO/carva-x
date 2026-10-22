package com.example.carva_x.service.serviceImpl;

import com.example.carva_x.entity.User;
import com.example.carva_x.exception.InternalServerErrorException;
import com.example.carva_x.exception.NotFoundException;
import com.example.carva_x.model.request.LoginResquest;
import com.example.carva_x.model.request.UserRegisterRequest;
import com.example.carva_x.model.response.LoginResponse;
import com.example.carva_x.repository.AuthRepository;
import com.example.carva_x.service.AuthService;
import com.example.carva_x.service.JwtService;
import lombok.RequiredArgsConstructor;
import com.example.carva_x.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public LoginResponse registerUser(UserRegisterRequest userRegisterRequest){

        try {
            authRepository.findUserByEmail(userRegisterRequest.getEmail())
                    .ifPresent(user -> { throw new BadRequestException("Username ya esta en uso");
                    });

            User user = User.builder()
                    .name(userRegisterRequest.getName())
                    .email(userRegisterRequest.getEmail())
                    .password(passwordEncoder.encode(userRegisterRequest.getPassword()))
                    .addres(userRegisterRequest.getAddres())
                    .role(userRegisterRequest.getRole())
                    .build();

            authRepository.save(user);

            return LoginResponse.builder()
                    .accessToken(jwtService.generateJwtToken(user))
                    .build();


        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    @Override
    public LoginResponse login(LoginResquest loginResquest){

        try {
            User user = authRepository.findUserByEmail(loginResquest.getEmail())
                    .orElseThrow(() -> new NotFoundException("Credenciales invalidas"));

            if(!passwordEncoder.matches(loginResquest.getPassword(),user.getPassword())){
                throw new NotFoundException("Credenciales invalidas");
            }

            return LoginResponse.builder()
                    .accessToken(jwtService.generateJwtToken(user))
                    .build();

        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

}