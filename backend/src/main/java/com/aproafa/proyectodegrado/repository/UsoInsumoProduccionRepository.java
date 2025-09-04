package com.aproafa.proyectodegrado.repository;

import com.aproafa.proyectodegrado.model.entity.UsoInsumoProduccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoInsumoProduccionRepository extends JpaRepository<UsoInsumoProduccion, Integer> {
  List<UsoInsumoProduccion> findByProduccionIdProduccion(Integer idProduccion);
}
