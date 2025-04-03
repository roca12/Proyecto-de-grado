package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialInsumoRepository extends JpaRepository<HistorialInsumo, Integer> {
  List<HistorialInsumo> findByInsumoIdInsumo(int idInsumo);
}
