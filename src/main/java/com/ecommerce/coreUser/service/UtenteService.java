package com.ecommerce.coreUser.service;

import com.ecommerce.coreUser.api.model.JwtResponse;
import com.ecommerce.coreUser.api.model.LoginRequest;
import com.ecommerce.coreUser.api.model.RegisterRequest;

import com.ecommerce.coreUser.api.model.UtenteDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public interface UtenteService {
    JwtResponse login(LoginRequest requestLogin) throws Exception;
    JwtResponse register(RegisterRequest utente) throws Exception;
    List<UtenteDto> getUsers(String nome, String cognome, String email, String numero, LocalDate inizio, LocalDate fine, String ruolo) throws Exception;
    UtenteDto updateUser(String id,UtenteDto utenteDto) throws Exception;
    void deleteUser(String id) throws Exception;
    boolean existsByUuid(String uuid);

}
