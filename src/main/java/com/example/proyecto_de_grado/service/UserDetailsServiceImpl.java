package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.repository.UsuarioRepository;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired private UsuarioRepository usuarioRepository;

  // Necesitamos un método para encontrar usuarios por ID
  public Optional<Usuario> findUsuarioById(int id) {
    return usuarioRepository.findById(id);
  }

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
    String rol = "ROLE_" + usuario.get().getTipoUsuario().toUpperCase();

    // Importante: Usamos el ID como username de forma consistente
    return new User(
        String.valueOf(userId),
        usuario.get().getContraseña(),
        Collections.singletonList(new SimpleGrantedAuthority(rol)));
  }
}
