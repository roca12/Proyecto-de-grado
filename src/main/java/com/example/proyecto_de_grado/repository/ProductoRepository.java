package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Producto.
 *
 * <p>Este repositorio extiende JpaRepository y proporciona métodos para interactuar con la base de
 * datos en lo que respecta a la entidad `Producto`. JpaRepository incluye operaciones CRUD básicas
 * como guardar, eliminar y buscar productos.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByFincaId(Integer idFinca);

}
