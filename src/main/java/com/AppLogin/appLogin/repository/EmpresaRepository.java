package com.AppLogin.appLogin.repository;

import com.AppLogin.appLogin.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
}
