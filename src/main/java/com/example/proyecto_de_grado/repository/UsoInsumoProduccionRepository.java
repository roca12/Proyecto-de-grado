package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.UsoInsumoProduccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsoInsumoProduccionRepository extends JpaRepository<UsoInsumoProduccion, Integer> {
  List<UsoInsumoProduccion> findByProduccionIdProduccion(Integer idProduccion);
}
