package com.aproafa.proyectodegrado.repository;

import com.aproafa.proyectodegrado.model.entity.Empleado;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Empleado}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, así como consultas
 * personalizadas para obtener un empleado basado en el id de la persona o su número de
 * identificación.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {

  /**
   * Busca un empleado a partir del id de la persona asociada.
   *
   * @param idPersona El identificador de la persona.
   * @return Un {@link Optional} que contiene el empleado si existe.
   */
  Optional<Empleado> findByPersona_IdPersona(int idPersona);

  /**
   * Busca un empleado a partir del número de identificación de la persona asociada.
   *
   * @param numeroIdentificacion El número de identificación de la persona.
   * @return Un {@link Optional} que contiene el empleado si existe.
   */
  Optional<Empleado> findByPersona_NumeroIdentificacion(String numeroIdentificacion);
}
