package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
  List<Insumo> findByProveedorIdProveedor(int idProveedor);
}
