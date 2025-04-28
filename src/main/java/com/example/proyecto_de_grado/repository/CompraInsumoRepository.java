package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link CompraInsumo}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de consultas personalizadas
 * para obtener las compras de insumos según el id del insumo o del proveedor.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Repository
public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Integer> {

    /**
     * Busca una lista de compras de insumo asociadas a un insumo específico.
     *
     * @param idInsumo El identificador del insumo.
     * @return Una lista de compras de insumo asociadas al insumo con el id especificado.
     */
    List<CompraInsumo> findByInsumoIdInsumo(int idInsumo);

    /**
     * Busca una lista de compras de insumo asociadas a un proveedor específico.
     *
     * @param idProveedor El identificador del proveedor.
     * @return Una lista de compras de insumo asociadas al proveedor con el id especificado.
     */
    List<CompraInsumo> findByProveedorIdProveedor(int idProveedor);
}
