package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.DetalleVenta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para operaciones de persistencia de detalles de venta.
 */
@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

  List<DetalleVenta> findByVenta_IdVenta(Integer idVenta);

  @Modifying
  @Query("DELETE FROM DetalleVenta d WHERE d.venta.idVenta = :idVenta")
  void deleteByVenta_IdVenta(@Param("idVenta") Integer idVenta);

  // Nuevo: buscar por producción
  List<DetalleVenta> findByIdProduccion(Integer idProduccion);
}
