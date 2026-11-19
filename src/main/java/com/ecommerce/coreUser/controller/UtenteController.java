package com.ecommerce.coreUser.controller;

import com.ecommerce.coreUser.api.UsersApi;
import com.ecommerce.coreUser.api.model.JwtResponse;
import com.ecommerce.coreUser.api.model.LoginRequest;
import com.ecommerce.coreUser.api.model.RegisterRequest;
import com.ecommerce.coreUser.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class UtenteController implements UsersApi {

    @Autowired
    private UtenteService utenteService ;

    @Override
    public ResponseEntity<JwtResponse> register(RegisterRequest dto) throws Exception {
        JwtResponse response = utenteService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Boolean> existsUserByUuid(String uuid) throws Exception {
        return ResponseEntity.ok(utenteService.existsByUuid(uuid));
    }

    @Override
    public ResponseEntity<JwtResponse> login(LoginRequest dto) throws Exception {
        JwtResponse response = utenteService.login(dto);
        return ResponseEntity.ok().body(response);
    }




}
