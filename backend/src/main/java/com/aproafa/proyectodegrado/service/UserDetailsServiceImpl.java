package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.entity.Usuario;
import com.aproafa.proyectodegrado.repository.UsuarioRepository;

import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de detalles del usuario para la autenticación con Spring Security.
 *
 * <p>Esta clase implementa la interfaz {@link UserDetailsService} para cargar el usuario desde la
 * base de datos usando su ID y convertirlo a un objeto {@link UserDetails} que será usado para la
 * autenticación en Spring Security.
 *
 * <p>Autor: Santiago Arias
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  /**
   * Método para encontrar un usuario por su ID.
   *
   * @param id El ID del usuario.
   * @return Un {@link Optional} que contiene el usuario si existe, o está vacío si no se encuentra.
   */
  public Optional<Usuario> findUsuarioById(int id) {
    return usuarioRepository.findById(id);
  }

  /**
   * Carga un {@link UserDetails} de Spring Security a partir del nombre de usuario (en este caso,
   * el ID del usuario).
   *
   * @param username El nombre de usuario (ID de usuario como String).
   * @return Un objeto {@link UserDetails} que contiene los detalles del usuario y su rol.
   * @throws UsernameNotFoundException Si no se encuentra un usuario con el ID proporcionado.
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    int userId;
    try {
      userId = Integer.parseInt(username);
    } catch (NumberFormatException e) {
      throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }

    Optional<Usuario> usuario = usuarioRepository.findById(userId);
    if (usuario.isEmpty()) {
      throw new UsernameNotFoundException("Usuario no encontrado con ID: " + userId);
    }

    // Convertir tipoUsuario a un rol de Spring Security
    String rol =
        "ROLE_" + usuario.get().getTipoUsuario().toUpperCase(); // Ej: "ADMIN" → "ROLE_ADMIN"

    // Importante: Usamos el ID como username de forma consistente
    return new User(
        String.valueOf(userId),
        usuario.get().getContraseña(),
        Collections.singletonList(new SimpleGrantedAuthority(rol)));
  }
}
