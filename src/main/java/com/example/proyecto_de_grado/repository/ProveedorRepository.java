package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Proveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Repositorio para operaciones CRUD sobre la entidad Proveedor. */
@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

  List<Proveedor> findByFinca_Id(Integer idFinca);
}
