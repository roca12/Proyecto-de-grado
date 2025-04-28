package com.example.proyecto_de_grado.repository;

import com.example.proyecto_de_grado.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Usuario}.
 *
 * <p>Proporciona operaciones CRUD estándar a través de {@link JpaRepository}, además de consultas personalizadas
 * para buscar un usuario por su ID de persona y por su número de identificación.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    /**
     * Busca un usuario por su ID de persona.
     *
     * @param idPersona El ID de la persona asociada al usuario.
     * @return Un {@link Optional} con el usuario si existe, o vacío si no se encuentra.
     */
    Optional<Usuario> findByIdPersona(int idPersona);

    /**
     * Busca un usuario por su número de identificación.
     *
     * @param numeroIdentificacion El número de identificación del usuario.
     * @return Un {@link Optional} con el usuario si existe, o vacío si no se encuentra.
     */
    Optional<Usuario> findByNumeroIdentificacion(String numeroIdentificacion);
}
