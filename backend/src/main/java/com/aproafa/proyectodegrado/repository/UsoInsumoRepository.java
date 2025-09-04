package com.aproafa.proyectodegrado.repository;

import com.aproafa.proyectodegrado.model.entity.UsoInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoInsumoRepository extends JpaRepository<UsoInsumo, Integer> {
  List<UsoInsumo> findByActividadIdActividad(Integer idActividad);
}
