package com.aproafa.proyectodegrado.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aproafa.proyectodegrado.model.entity.InventarioProducto;

/** Repositorio para acceder a datos del inventario de productos. */
@Repository
public interface InventarioProductoRepository extends JpaRepository<InventarioProducto, Integer> {

  /**
   * Busca el inventario correspondiente a un producto por su ID.
   *
   * @param idProducto ID del producto.
   * @return Inventario del producto si existe.
   */
  Optional<InventarioProducto> findByProducto_IdProducto(Integer idProducto);
}
