package com.ecommerce.authService.repository;

import com.ecommerce.authService.entity.Utente;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UtenteRepository extends MongoRepository<Utente, String> {

   Utente findByEmail(String email);
   Utente findByUuid(String uuid);

}