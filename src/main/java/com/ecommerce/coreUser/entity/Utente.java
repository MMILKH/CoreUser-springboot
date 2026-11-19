package com.ecommerce.coreUser.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Document(collection = "utenti")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utente implements UserDetails {

    @Id
    private String id;

    private String uuid = UUID.randomUUID().toString();

    private String nome;
    private String cognome;
    private String email;
    private String numero;
    private String indirizzo;
    private String passwordu;

    private String ruolo;

    private LocalDateTime dataCreazione = LocalDateTime.now();
    private LocalDateTime dataUltimaModifica;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (ruolo == null || ruolo.isEmpty()) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority(ruolo.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return this.passwordu;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }

}