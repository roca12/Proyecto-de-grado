package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio para la entidad {@link Venta}. Proporciona métodos CRUD y operaciones adicionales
 * para acceder a los datos de ventas en la base de datos.
 *
 * <p>Extiende {@link JpaRepository} para heredar operaciones estándar como guardar, eliminar,
 * buscar por ID, etc.
 *
 * @author Anderson Zuluaga
 */
public interface VentaRepository extends JpaRepository<Venta, Integer> {}
