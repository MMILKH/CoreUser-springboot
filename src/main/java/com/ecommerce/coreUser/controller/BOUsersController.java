package com.ecommerce.coreUser.controller;

import com.ecommerce.coreUser.api.AdminApi;
import com.ecommerce.coreUser.api.model.UtenteDto;
import com.ecommerce.coreUser.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")

public class BOUsersController implements AdminApi {

    @Autowired
    private UtenteService utenteService;



    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(String id) throws Exception {
        utenteService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }



    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UtenteDto>> searchUserAdmin(String email, String nome, String cognome, String numero, String ruolo,LocalDate dataInizio, LocalDate dataFine) throws Exception {
        List<UtenteDto> utenti = utenteService.getUsers(nome, cognome, email, numero, dataInizio, dataFine, ruolo);
        return ResponseEntity.ok().body(utenti);
    }


    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UtenteDto> updateUser(String id, UtenteDto utenteDto) throws Exception {
        UtenteDto response = utenteService.updateUser(id, utenteDto);
        return ResponseEntity.ok().body(response);
    }


}