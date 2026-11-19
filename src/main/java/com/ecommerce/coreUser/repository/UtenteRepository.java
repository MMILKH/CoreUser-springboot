package com.ecommerce.coreUser.repository;

import com.ecommerce.coreUser.entity.Utente;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UtenteRepository extends MongoRepository<Utente, String> {

   Utente findByEmail(String email);
   Utente findByUuid(String uuid);
   void  deleteByUuid(String uuid);
   boolean existsByUuid(String uuid);


}