package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FilialRepository extends JpaRepository<Filial, Integer> {

    List<Filial> findByEmpresa_Idempresa(Integer idempresa);
}
