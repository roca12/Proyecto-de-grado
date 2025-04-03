package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Integer> {
  List<CompraInsumo> findByInsumoIdInsumo(int idInsumo);

  List<CompraInsumo> findByProveedorIdProveedor(int idProveedor);
}
