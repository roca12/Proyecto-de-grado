package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones de persistencia de detalles de venta.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    /**
     * Busca todos los detalles de venta asociados a una venta específica.
     *
     * @param idVenta Identificador de la venta
     * @return Lista de detalles de venta
     */
    List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);

    /**
     * Elimina todos los detalles de venta asociados a una venta específica.
     *
     * @param idVenta Identificador de la venta
     */
    @Modifying
    @Query("DELETE FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta")
    void deleteByVenta_IdVenta(@Param("idVenta") Integer idVenta);

    /**
     * Busca detalles de venta por producto.
     *
     * @param idProducto Identificador del producto
     * @return Lista de detalles de venta que contienen el producto
     */
    List<DetalleVenta> findByIdProducto(Integer idProducto);
}