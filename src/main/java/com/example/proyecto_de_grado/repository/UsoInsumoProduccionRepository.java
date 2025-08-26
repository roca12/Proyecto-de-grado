package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.UsoInsumoProduccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UsoInsumoProduccionRepository extends JpaRepository<UsoInsumoProduccion, Integer> {
    List<UsoInsumoProduccion> findByProduccionIdProduccion(Integer idProduccion);
}