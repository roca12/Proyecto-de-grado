package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.Persona;
import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.repository.PersonaRepository;
import com.example.proyecto_de_grado.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de usuarios en la aplicación.
 *
 * <p>Esta clase ofrece métodos para realizar operaciones relacionadas con usuarios, como obtener,
 * guardar, eliminar y verificar la existencia de un usuario. También permite la relación de los
 * usuarios con personas, facilitando la interacción entre estas entidades.
 *
 * <p>Autor: Anderson Zuluaga - Santiago Arias
 */
@Service
public class UsuarioService {

  @Autowired private UsuarioRepository usuarioRepository;

  @Autowired private PersonaRepository personaRepository;

  /**
   * Obtiene todos los usuarios registrados.
   *
   * @return Una lista de todos los usuarios.
   */
  public List<Usuario> getAllUsuarios() {
    return usuarioRepository.findAll();
  }

  /**
   * Obtiene un usuario por su ID.
   *
   * @param id El ID del usuario a buscar.
   * @return Un {@link Optional} que contiene el usuario si se encuentra, o vacío si no existe.
   */
  public Optional<Usuario> getUsuarioById(int id) {
    return usuarioRepository.findById(id);
  }

  /**
   * Guarda un nuevo usuario en la base de datos.
   *
   * @param usuario El usuario a guardar.
   * @return El usuario guardado con su ID asignado.
   */
  public Usuario saveUsuario(Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  /**
   * Elimina un usuario por su ID.
   *
   * @param id El ID del usuario a eliminar.
   */
  public void deleteUsuario(int id) {
    usuarioRepository.deleteById(id);
  }

  /**
   * Verifica si ya existe un usuario asociado con una persona dada por su ID.
   *
   * @param idPersona El ID de la persona a verificar.
   * @return true si el usuario existe, false si no existe.
   */
  public boolean existsByIdPersona(int idPersona) {
    return usuarioRepository.findByIdPersona(idPersona).isPresent();
  }

  /**
   * Obtiene una persona asociada a un usuario mediante el ID de la persona.
   *
   * @param idPersona El ID de la persona.
   * @return Un {@link Optional} que contiene la persona si se encuentra, o vacío si no existe.
   */
  public Optional<Persona> getPersonaById(int idPersona) {
    return personaRepository.findById(idPersona);
  }

  /**
   * Obtiene un usuario por su número de identificación.
   *
   * @param numeroIdentificacion El número de identificación del usuario.
   * @return Un {@link Optional} que contiene el usuario si se encuentra, o vacío si no existe.
   */
  public Optional<Usuario> getUsuarioByNumeroIdentificacion(String numeroIdentificacion) {
    return usuarioRepository.findByNumeroIdentificacion(numeroIdentificacion);
  }
}
