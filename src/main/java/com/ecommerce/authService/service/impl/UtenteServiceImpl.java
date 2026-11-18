package com.ecommerce.authService.service.impl;


import com.ecommerce.authService.api.model.JwtResponse;
import com.ecommerce.authService.api.model.LoginRequest;
import com.ecommerce.authService.api.model.RegisterRequest;
import com.ecommerce.authService.api.model.UtenteDto;
import com.ecommerce.authService.entity.Utente;
import com.ecommerce.authService.repository.UtenteRepository;
import com.ecommerce.authService.security.JwtUtils;
import com.ecommerce.authService.service.UtenteService;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;

    public UtenteServiceImpl(UtenteRepository utenteRepository,
                             ModelMapper modelMapper,
                             PasswordEncoder passwordEncoder,
                             JwtUtils jwtService,
                             AuthenticationManager authenticationManager) {
        this.utenteRepository = utenteRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public JwtResponse login(LoginRequest requestLogin) {
        if (requestLogin == null || requestLogin.getEmail() == null || requestLogin.getPassword() == null) {
            throw new IllegalArgumentException("Campo/i non valido/i");
        }


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestLogin.getEmail(),
                        requestLogin.getPassword()
                )
        );


        Utente utente = utenteRepository.findByEmail(requestLogin.getEmail());
        if (utente == null) {
            throw new IllegalArgumentException("Email non esistente");
        }


        String tokenJwt = jwtService.generateToken(utente);


        UtenteDto utenteDto = modelMapper.map(utente, UtenteDto.class);

        JwtResponse response = new JwtResponse();
        response.setToken(tokenJwt);
        response.setUtente(utenteDto);
        return response;
    }

    @Override
    public JwtResponse register(RegisterRequest dto) {

        if (dto.getPassword() == null || dto.getPassword().isEmpty() ||
                dto.getEmail() == null || dto.getEmail().isEmpty() ||
                dto.getNome() == null || dto.getNome().isEmpty() ||
                dto.getCognome() == null || dto.getCognome().isEmpty() ||
                dto.getNumero() == null || dto.getNumero().isEmpty() ||
                dto.getPasswordConf() == null || dto.getPasswordConf().isEmpty()) {
            throw new IllegalArgumentException("Campo/i non valido/i");
        }

        if (!dto.getPassword().equals(dto.getPasswordConf())) {
            throw new IllegalArgumentException("Le password non coincidono");
        }

        if (utenteRepository.findByEmail(dto.getEmail())!= null) {
            throw new IllegalArgumentException("Email già registrata nel sistema");
        }


        Utente utente = new Utente();
        utente.setNome(dto.getNome());
        utente.setCognome(dto.getCognome());
        utente.setEmail(dto.getEmail());
        utente.setNumero(dto.getNumero());
        utente.setIndirizzo(dto.getIndirizzo());

        utente.setDataCreazione(LocalDateTime.now());
        utente.setDataUltimaModifica(LocalDateTime.now());
        utente.setPasswordu(passwordEncoder.encode(dto.getPassword()));


        utente.setRuolo("customer");

        Utente utenteSalvato = utenteRepository.save(utente);


        String tokenJwt = jwtService.generateToken(utenteSalvato);
        UtenteDto utenteDto = modelMapper.map(utenteSalvato, UtenteDto.class);

        JwtResponse response = new JwtResponse();
        response.setToken(tokenJwt);
        response.setUtente(utenteDto);
        return response;
    }
}