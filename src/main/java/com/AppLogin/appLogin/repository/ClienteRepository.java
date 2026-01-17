package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findByNomeContainingIgnoreCase(String nome);

    List<Cliente> findByCpfContaining(String cpf);
    Optional<Cliente> findFirstByCpfAndStatus(String cpf, String status);
    Optional<Cliente> findFirstByCnpjAndStatus(String cnpj, String status);
    Optional<Cliente> findFirstByNomeAndStatus(String nome, String status);

}
