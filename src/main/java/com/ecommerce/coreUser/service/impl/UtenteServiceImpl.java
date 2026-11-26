package com.ecommerce.coreUser.service.impl;


import com.ecommerce.coreUser.Specification.UtenteCriteriaBuilder;
import com.ecommerce.coreUser.api.model.JwtResponse;
import com.ecommerce.coreUser.api.model.LoginRequest;
import com.ecommerce.coreUser.api.model.RegisterRequest;
import com.ecommerce.coreUser.api.model.UtenteDto;
import com.ecommerce.coreUser.entity.Utente;
import com.ecommerce.coreUser.repository.UtenteRepository;
import com.ecommerce.coreUser.security.JwtUtils;
import com.ecommerce.coreUser.service.UtenteService;
import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepository utenteRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtService;
    private final AuthenticationManager authenticationManager;
    private final MongoTemplate mongoTemplate;

    public UtenteServiceImpl(UtenteRepository utenteRepository,
                             ModelMapper modelMapper,
                             PasswordEncoder passwordEncoder,
                             JwtUtils jwtService,
                             AuthenticationManager authenticationManager, MongoTemplate mongoTemplate) {
        this.utenteRepository = utenteRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.mongoTemplate = mongoTemplate;
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
        response.setMustChangePassword(utente.isTemporaryPassword());
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

        if(Boolean.TRUE.equals(dto.getTemporaryPassword())) {
            utente.setTemporaryPassword(true);
        }else if (dto.getTemporaryPassword()==null || Boolean.FALSE.equals(dto.getTemporaryPassword())) {
            utente.setTemporaryPassword(false);
        }

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
        response.setMustChangePassword(utente.isTemporaryPassword());
        return response;
    }




    @Override
    public List<UtenteDto> getUsers(String nome, String cognome, String email, String numero, LocalDate inizio, LocalDate fine, String ruolo) {

        List<Criteria> filters = UtenteCriteriaBuilder.buildAll(
                nome, cognome, email, ruolo, numero, inizio, fine
        );

        Query query = new Query();
        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }


        query.fields().exclude("passwordu");

        List<Utente> utenti = mongoTemplate.find(query, Utente.class);

        return utenti.stream()
                .map(u -> modelMapper.map(u, UtenteDto.class))
                .toList();
    }


    @Override
    public UtenteDto updateUser(String id, UtenteDto utenteDto) throws Exception {
        Utente utenteEsistente = utenteRepository.findByUuid(utenteDto.getUuid());
        utenteEsistente.setNome(utenteDto.getNome());
        utenteEsistente.setCognome(utenteDto.getCognome());
        utenteEsistente.setEmail(utenteDto.getEmail());
        utenteEsistente.setRuolo(utenteDto.getRuolo());
        utenteEsistente.setDataUltimaModifica(LocalDateTime.now());
        utenteRepository.save(utenteEsistente);
        return modelMapper.map(utenteEsistente, UtenteDto.class);
    }

    @Override
    public void deleteUser(String uuid) throws Exception {
        Utente utenteEsistente = utenteRepository.findByUuid(uuid);
        if (utenteEsistente != null) {
            utenteRepository.deleteByUuid(uuid);
        }
    }



    @Override
    public boolean existsByUuid(String uuid) {
        return utenteRepository.existsByUuid(uuid);
    }
}