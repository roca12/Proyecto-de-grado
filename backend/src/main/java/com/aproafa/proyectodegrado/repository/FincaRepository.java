package com.aproafa.proyectodegrado.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.aproafa.proyectodegrado.model.entity.Finca;

/**
 * Repositorio para la entidad {@link Finca}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, así como consultas
 * personalizadas para obtener una finca a partir de su encargado o su id.
 *
 * <p>Autor: Anderson Zuluaga
 */
public interface FincaRepository extends JpaRepository<Finca, Integer> {

  /**
   * Busca una finca a partir del encargado asociado.
   *
   * @param encargado El nombre del encargado de la finca.
   * @return Un {@link Optional} que contiene la finca si existe.
   */
  Optional<Finca> findByEncargado(String encargado);

  /**
   * Busca una finca a partir de su id.
   *
   * @param id_finca El identificador de la finca.
   * @return Un {@link Optional} que contiene la finca si existe.
   */
  Optional<Finca> findById(Integer id_finca);
}
