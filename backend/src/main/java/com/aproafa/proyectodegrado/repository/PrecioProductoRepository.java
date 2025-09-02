package com.aproafa.proyectodegrado.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aproafa.proyectodegrado.model.entity.PrecioProducto;

/**
 * Repositorio JPA para la entidad PrecioProducto.
 *
 * <p>Autor: Anderson Zuluaga
 */
public interface PrecioProductoRepository extends JpaRepository<PrecioProducto, Integer> {

  /**
   * Busca el precio actual (sin fecha de fin) para un producto específico.
   *
   * @param idProducto ID del producto.
   * @return PrecioProducto si existe.
   */
  Optional<PrecioProducto> findByProducto_IdProductoAndFechaFinIsNull(Integer idProducto);
}
