package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialInsumoRepository extends JpaRepository<HistorialInsumo, Integer> {
    List<HistorialInsumo> findByInsumoIdInsumo(int idInsumo);
}
