package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Insumo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Insumo}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de una
 * consulta personalizada para obtener los insumos asociados a un proveedor específico.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Integer> {

  /**
   * Busca una lista de insumos asociados a un proveedor específico.
   *
   * @param idProveedor El identificador del proveedor.
   * @return Una lista de insumos asociados al proveedor con el id especificado.
   */
  List<Insumo> findByProveedorIdProveedor(int idProveedor);
}
