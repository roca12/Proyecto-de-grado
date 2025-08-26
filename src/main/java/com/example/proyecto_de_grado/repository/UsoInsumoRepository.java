package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.UsoInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Integer> {
    List<UsoInsumo> findByActividadIdActividad(Integer idActividad);
}

