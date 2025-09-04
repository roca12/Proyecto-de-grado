package com.aproafa.proyectodegrado.repository;

import com.aproafa.proyectodegrado.model.entity.Persona;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la entidad {@link Persona}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de consultas
 * personalizadas para verificar la existencia de una persona por su número de identificación y para
 * buscar una persona por este mismo número.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Integer> {

  /**
   * Verifica si existe una persona con el número de identificación especificado.
   *
   * @param numeroIdentificacion El número de identificación de la persona.
   * @return {@code true} si existe una persona con el número de identificación, {@code false} en
   *     caso contrario.
   */
  boolean existsByNumeroIdentificacion(String numeroIdentificacion);

  /**
   * Busca una persona por su número de identificación.
   *
   * @param numeroIdentificacion El número de identificación de la persona.
   * @return Un {@link Optional} con la persona si existe, o vacío si no se encuentra.
   */
  Optional<Persona> findByNumeroIdentificacion(String numeroIdentificacion);
}
