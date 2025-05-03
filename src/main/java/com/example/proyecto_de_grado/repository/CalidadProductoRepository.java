package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.CalidadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad CalidadProducto.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface CalidadProductoRepository extends JpaRepository<CalidadProducto, Integer> {}
