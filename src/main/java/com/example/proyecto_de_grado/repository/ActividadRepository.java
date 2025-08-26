package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Actividad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Actividad}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de la
 * posibilidad de realizar consultas específicas sobre las actividades relacionadas con una finca.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

  /**
   * Obtiene una lista de actividades asociadas a una finca específica.
   *
   * @param idFinca El identificador de la finca.
   * @return Una lista de actividades asociadas a la finca con el id especificado.
   */
  List<Actividad> findByIdFinca(Integer idFinca);
}
