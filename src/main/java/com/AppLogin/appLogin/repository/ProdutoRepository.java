package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // Buscar por nome (contém, ignorando maiúsculas/minúsculas)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Buscar por tipo
    List<Produto> findByTipo(String tipo);

    // Buscar por grupo
    List<Produto> findByGrupo(String grupo);
}
