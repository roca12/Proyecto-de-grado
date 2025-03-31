package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        System.out.println("Intento de login con ID: " + usuario.getIdPersona() + " y contraseña: " + usuario.getContraseña());

        Optional<Usuario> usuarioEncontrado = usuarioService.getUsuarioById(usuario.getIdPersona());

        if (usuarioEncontrado.isPresent()) {
            System.out.println("Usuario encontrado: " + usuarioEncontrado.get().getIdPersona());
            System.out.println("Contraseña guardada: " + usuarioEncontrado.get().getContraseña());

            if (usuarioEncontrado.get().getContraseña().equals(usuario.getContraseña())) {
                System.out.println("Login exitoso");
                return ResponseEntity.ok(usuarioEncontrado.get());
            } else {
                System.out.println("Contraseña incorrecta");
            }
        } else {
            System.out.println("Usuario no encontrado");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID o contraseña incorrectos");
    }

}
