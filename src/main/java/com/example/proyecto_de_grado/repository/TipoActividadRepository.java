package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.TipoActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link TipoActividad}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de consultas personalizadas
 * para verificar la existencia de un tipo de actividad por su nombre y para buscar un tipo de actividad por
 * nombre.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Repository
public interface TipoActividadRepository extends JpaRepository<TipoActividad, Integer> {

    /**
     * Busca un tipo de actividad por su nombre.
     *
     * @param nombre El nombre del tipo de actividad.
     * @return Un {@link Optional} con el tipo de actividad si existe, o vacío si no se encuentra.
     */
    Optional<TipoActividad> findByNombre(String nombre);

    /**
     * Verifica si existe un tipo de actividad con el nombre especificado.
     *
     * @param nombre El nombre del tipo de actividad.
     * @return {@code true} si existe un tipo de actividad con el nombre especificado, {@code false} en caso contrario.
     */
    boolean existsByNombre(String nombre);
}
