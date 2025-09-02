package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.UsoInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Integer> {
  List<UsoInsumo> findByActividadIdActividad(Integer idActividad);
}
