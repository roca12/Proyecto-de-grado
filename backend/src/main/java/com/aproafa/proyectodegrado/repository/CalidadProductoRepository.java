package com.aproafa.proyectodegrado.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aproafa.proyectodegrado.model.entity.CalidadProducto;

/**
 * Repositorio JPA para la entidad CalidadProducto.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface CalidadProductoRepository extends JpaRepository<CalidadProducto, Integer> {}
