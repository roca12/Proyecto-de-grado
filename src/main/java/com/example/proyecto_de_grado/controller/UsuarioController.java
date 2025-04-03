package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.AuthenticationRequest;
import com.example.proyecto_de_grado.model.entity.AuthenticationResponse;
import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.security.JwtUtil;
import com.example.proyecto_de_grado.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios") // Ruta base para los usuarios
public class UsuarioController {

  @Autowired private UsuarioService usuarioService;

  @Autowired private JwtUtil jwtUtil;

  @Autowired private AuthenticationManager authenticationManager;

  // Obtener todos los usuarios
  @GetMapping
  public List<Usuario> getAllUsuarios() {
    return usuarioService.getAllUsuarios();
  }

  // Obtener usuario por ID
  @GetMapping("/{id}")
  public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
    Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
    return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // Guardar un usuario
  @PostMapping
  public Usuario saveUsuario(@RequestBody Usuario usuario) {
    return usuarioService.saveUsuario(usuario);
  }

  // Eliminar un usuario por ID
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
    usuarioService.deleteUsuario(id);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) {
    System.out.println("Intento de login con ID: " + authRequest.getIdPersona());

    try {
      // Autenticar con Spring Security
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              String.valueOf(authRequest.getIdPersona()), authRequest.getContraseña()));
    } catch (BadCredentialsException e) {
      System.out.println("Credenciales inválidas");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID o contraseña incorrectos");
    }

    // Si llegamos aquí, la autenticación fue exitosa
    Optional<Usuario> usuarioOpt = usuarioService.getUsuarioById(authRequest.getIdPersona());
    if (usuarioOpt.isPresent()) {
      Usuario usuario = usuarioOpt.get();

      // Generar token JWT
      String jwt =
          jwtUtil.generateToken(
              String.valueOf(usuario.getIdPersona()),
              usuario.getTipoUsuario(),
              usuario.getIdPersona());

      // Crear respuesta con token y datos básicos del usuario
      AuthenticationResponse response =
          new AuthenticationResponse(
              jwt,
              usuario.getIdPersona(),
              usuario.getNombre(),
              usuario.getApellido(),
              usuario.getTipoUsuario());

      System.out.println("Login exitoso para usuario: " + usuario.getIdPersona());
      return ResponseEntity.ok(response);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado");
  }
}
