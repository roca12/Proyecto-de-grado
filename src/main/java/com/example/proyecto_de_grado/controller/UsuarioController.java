package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/usuarios") // Ruta base para los usuarios
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> getAllUsuarios() {
        return usuarioService.getAllUsuarios();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public Optional<Usuario> getUsuarioById(@PathVariable int id) {
        return usuarioService.getUsuarioById(id);
    }

    // Guardar un usuario
    @PostMapping
    public Usuario saveUsuario(@RequestBody Usuario usuario) {
        return usuarioService.saveUsuario(usuario);
    }

    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public void deleteUsuario(@PathVariable int id) {
        usuarioService.deleteUsuario(id);
    }
}
