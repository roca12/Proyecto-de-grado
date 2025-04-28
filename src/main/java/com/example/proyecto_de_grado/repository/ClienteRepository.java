package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Cliente}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository},
 * así como consultas personalizadas para obtener un cliente basado en el id de la persona
 * o su número de identificación.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    /**
     * Busca un cliente a partir del id de la persona asociada.
     *
     * @param idPersona El identificador de la persona.
     * @return Un {@link Optional} que contiene el cliente si existe.
     */
    Optional<Cliente> findByPersona_IdPersona(int idPersona);

    /**
     * Busca un cliente a partir del número de identificación de la persona asociada.
     *
     * @param numeroIdentificacion El número de identificación de la persona.
     * @return Un {@link Optional} que contiene el cliente si existe.
     */
    Optional<Cliente> findByPersona_NumeroIdentificacion(String numeroIdentificacion);
}
