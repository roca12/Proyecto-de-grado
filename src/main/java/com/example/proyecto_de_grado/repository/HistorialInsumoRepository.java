package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para la entidad {@link HistorialInsumo}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de una consulta personalizada
 * para obtener el historial de uso de un insumo específico.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Repository
public interface HistorialInsumoRepository extends JpaRepository<HistorialInsumo, Integer> {

    /**
     * Busca una lista de registros de historial de insumos asociados a un insumo específico.
     *
     * @param idInsumo El identificador del insumo.
     * @return Una lista de registros de historial de insumo asociados al insumo con el id especificado.
     */
    List<HistorialInsumo> findByInsumoIdInsumo(int idInsumo);
}
