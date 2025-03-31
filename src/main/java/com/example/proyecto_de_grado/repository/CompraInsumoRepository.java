package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Integer> {
    List<CompraInsumo> findByInsumoIdInsumo(int idInsumo);
    List<CompraInsumo> findByProveedorIdProveedor(int idProveedor);
}
