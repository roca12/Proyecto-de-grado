package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.PrecioProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 * Repositorio JPA para la entidad PrecioProducto.
 * <p>Autor: Anderson Zuluaga
 */
public interface PrecioProductoRepository extends JpaRepository<PrecioProducto, Integer> {

    /**
     * Busca el precio actual (sin fecha de fin) para un producto específico.
     * @param idProducto ID del producto.
     * @return PrecioProducto si existe.
     */
    Optional<PrecioProducto> findByProducto_IdProductoAndFechaFinIsNull(Integer idProducto);
}