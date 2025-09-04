package com.aproafa.proyectodegrado.repository;

import com.aproafa.proyectodegrado.model.entity.Produccion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Producción.
 *
 * <p>Este repositorio extiende JpaRepository y proporciona métodos para interactuar con la base de
 * datos en lo que respecta a la entidad `Produccion`. JpaRepository incluye operaciones CRUD
 * básicas como guardar, eliminar y buscar producciones. Además, incluye métodos personalizados,
 * como el que busca producciones por ID de finca.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface ProduccionRepository extends JpaRepository<Produccion, Integer> {

  /**
   * Busca todas las producciones asociadas a un ID de finca específico.
   *
   * <p>Este método permite obtener una lista de producciones que están vinculadas a una finca
   * particular. Utiliza la relación entre la entidad `Produccion` y la entidad `Finca` para
   * realizar la búsqueda.
   *
   * @param idFinca ID de la finca para la cual se desean obtener las producciones.
   * @return Lista de producciones asociadas al ID de finca proporcionado.
   */
  List<Produccion> findByFinca_Id(Integer idFinca);
}
