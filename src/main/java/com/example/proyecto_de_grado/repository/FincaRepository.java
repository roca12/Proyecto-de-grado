package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Finca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio para la entidad {@link Finca}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository},
 * así como consultas personalizadas para obtener una finca a partir de su encargado o su id.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
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
