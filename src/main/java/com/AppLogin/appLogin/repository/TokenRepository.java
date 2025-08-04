package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.RecuperaSenhaToken;
import com.AppLogin.appLogin.model.Usuario;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends CrudRepository<RecuperaSenhaToken, Long> {

    RecuperaSenhaToken findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM RecuperaSenhaToken t WHERE t.usuario = :usuario")
    void deleteByUsuario(Usuario usuario);

}
