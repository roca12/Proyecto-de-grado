package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {
    List<Insumo> findByProveedorIdProveedor(int idProveedor);
}
