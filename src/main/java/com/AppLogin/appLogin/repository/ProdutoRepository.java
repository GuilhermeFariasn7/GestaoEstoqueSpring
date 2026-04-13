package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Produto;
import com.AppLogin.appLogin.model.TipoProduto;
import com.AppLogin.appLogin.model.GrupoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Buscar por nome (contém, ignorando maiúsculas/minúsculas)
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Buscar por tipo (passando o objeto, não String)
    List<Produto> findByTipo(TipoProduto tipo);

    // Buscar por grupo (passando o objeto, não String)
    List<Produto> findByGrupo(GrupoProduto grupo);
}