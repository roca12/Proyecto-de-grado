package com.aproafa.proyectodegrado.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aproafa.proyectodegrado.model.entity.Venta;

/**
 * Repositorio para la entidad {@link Venta}. Proporciona métodos CRUD y operaciones adicionales
 * para acceder a los datos de ventas en la base de datos.
 *
 * <p>Extiende {@link JpaRepository} para heredar operaciones estándar como guardar, eliminar,
 * buscar por ID, etc.
 *
 * @author Anderson Zuluaga
 */
public interface VentaRepository extends JpaRepository<Venta, Integer> {

  List<Venta> findByFinca_Id(Integer idFinca);
}
