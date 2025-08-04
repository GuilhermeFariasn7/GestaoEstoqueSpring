package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {

    Usuario findById(long Id);

    Usuario findByEmail(String email);
}
