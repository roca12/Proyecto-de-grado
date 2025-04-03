package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

  @Autowired private UsuarioRepository usuarioRepository;

  public List<Usuario> getAllUsuarios() {
    List<Usuario> usuarios = usuarioRepository.findAll();
    System.out.println("Usuarios encontrados: " + usuarios);
    return usuarios;
  }

  public Optional<Usuario> getUsuarioById(int id) {
    return usuarioRepository.findById(id);
  }

  public Usuario saveUsuario(Usuario usuario) {
    return usuarioRepository.save(usuario);
  }

  public void deleteUsuario(int id) {
    usuarioRepository.deleteById(id);
  }
}
