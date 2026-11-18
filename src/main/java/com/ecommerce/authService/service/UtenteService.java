package com.ecommerce.authService.service;

import com.ecommerce.authService.api.model.JwtResponse;
import com.ecommerce.authService.api.model.LoginRequest;
import com.ecommerce.authService.api.model.RegisterRequest;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public interface UtenteService {
    JwtResponse login(LoginRequest requestLogin) throws Exception;
    JwtResponse register(RegisterRequest utente) throws Exception;

}
