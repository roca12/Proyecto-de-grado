package com.aproafa.proyectodegrado.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aproafa.proyectodegrado.model.entity.UsoInsumoProduccion;

@Repository
public interface UsoInsumoProduccionRepository extends JpaRepository<UsoInsumoProduccion, Integer> {
  List<UsoInsumoProduccion> findByProduccionIdProduccion(Integer idProduccion);
}
