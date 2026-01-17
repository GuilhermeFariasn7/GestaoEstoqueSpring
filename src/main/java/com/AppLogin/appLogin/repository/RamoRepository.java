package com.AppLogin.appLogin.repository;
import com.AppLogin.appLogin.model.Ramo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RamoRepository extends JpaRepository<Ramo,Integer>{

}
