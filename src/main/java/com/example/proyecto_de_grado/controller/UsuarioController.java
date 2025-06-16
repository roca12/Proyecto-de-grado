package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.RegistroRequest;
import com.example.proyecto_de_grado.model.entity.AuthenticationRequest;
import com.example.proyecto_de_grado.model.entity.AuthenticationResponse;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.Usuario;
import com.example.proyecto_de_grado.repository.FincaRepository;
import com.example.proyecto_de_grado.repository.PersonaRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de usuarios y autenticación.
 *
 * <p>Proporciona endpoints para:
 *
 * <ul>
 *   <li>Registro y autenticación de usuarios
 *   <li>Gestión de cuentas de usuario (CRUD)
 *   <li>Generación de tokens JWT para autenticación
 * </ul>
 *
 * @author Anderson Zuluaga - Santiago Arias
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

  @Autowired private UsuarioService usuarioService;

  @Autowired private PersonaRepository personaRepository;

  @Autowired private FincaRepository fincaRepository;

  @Autowired private JwtUtil jwtUtil;

  @Autowired private AuthenticationManager authenticationManager;

  @Autowired private PasswordEncoder passwordEncoder;

  /**
   * Obtiene todos los usuarios registrados en el sistema.
   *
   * @return Lista completa de usuarios con sus datos básicos
   */
  @GetMapping
  public List<Usuario> getAllUsuarios() {
    return usuarioService.getAllUsuarios();
  }

  /**
   * Obtiene un usuario específico por su ID.
   *
   * @param id Identificador único del usuario
   * @return ResponseEntity con el usuario encontrado (200 OK) o 404 si no existe
   */
  @GetMapping("/{id}")
  public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {
    Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
    return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Registra un nuevo usuario en el sistema.
   *
   * <p>Realiza validaciones para:
   *
   * <ul>
   *   <li>Tipo de usuario (ADMIN o USER)
   *   <li>Existencia previa de la persona
   *   <li>Existencia de la finca asociada
   * </ul>
   *
   * @param request Objeto con los datos de registro del usuario
   * @return ResponseEntity con el usuario creado (201 Created) o mensaje de error (400 Bad Request)
   */
  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegistroRequest request) {
    // Validar tipo de usuario
    if (!request.getTipoUsuario().equalsIgnoreCase("ADMIN")
        && !request.getTipoUsuario().equalsIgnoreCase("USER")) {
      return ResponseEntity.badRequest().body("Tipo de usuario inválido.");
    }

    // Validar si ya existe persona por número de identificación
    if (personaRepository.existsByNumeroIdentificacion(request.getNumeroIdentificacion())) {
      return ResponseEntity.badRequest().body("La persona ya está registrada.");
    }

    // Validar existencia de finca
    Optional<Finca> fincaOptional = fincaRepository.findById(request.getFincaId());
    if (fincaOptional.isEmpty()) {
      return ResponseEntity.badRequest().body("La finca indicada no existe.");
    }

    // Crear nuevo usuario (que hereda de Persona)
    Usuario usuario = new Usuario();
    usuario.setNombre(request.getNombre());
    usuario.setApellido(request.getApellido());
    usuario.setTipoId(request.getTipoId());
    usuario.setNumeroIdentificacion(request.getNumeroIdentificacion());
    usuario.setEmail(request.getEmail());
    usuario.setTelefono(request.getTelefono());
    usuario.setDireccion(request.getDireccion());
    usuario.setTipoUsuario(request.getTipoUsuario());
    usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
    usuario.setFinca(fincaOptional.get()); // Asignar finca existente

    // Guardar el usuario (y automáticamente la persona)
    Usuario nuevoUsuario = usuarioService.saveUsuario(usuario);

    return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
  }

  /**
   * Elimina un usuario del sistema.
   *
   * @param id Identificador del usuario a eliminar
   * @return ResponseEntity vacío con estado 200 OK
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
    usuarioService.deleteUsuario(id);
    return ResponseEntity.ok().build();
  }

  /**
   * Autentica un usuario y genera un token JWT.
   *
   * <p>El proceso de autenticación incluye:
   *
   * <ul>
   *   <li>Validación de credenciales con Spring Security
   *   <li>Generación de token JWT con información del usuario
   *   <li>Respuesta con token y datos básicos del usuario
   * </ul>
   *
   * @param authRequest Objeto con credenciales de autenticación (ID y contraseña)
   * @return ResponseEntity con token JWT y datos del usuario (200 OK) o mensaje de error (401
   *     Unauthorized)
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) {
    String numeroIdentificacion = authRequest.getNumeroIdentificacion();
    String contraseña = authRequest.getContraseña();

    Optional<Usuario> usuarioOpt =
        usuarioService.getUsuarioByNumeroIdentificacion(numeroIdentificacion);

    if (usuarioOpt.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body("Número de identificación no encontrado");
    }

    Usuario usuario = usuarioOpt.get();

    try {
      // Validar contraseña con Spring Security
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              String.valueOf(usuario.getIdPersona()), contraseña));
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Contraseña incorrecta");
    }

    // Generar token
    String jwt =
        jwtUtil.generateToken(
            String.valueOf(usuario.getIdPersona()),
            usuario.getTipoUsuario(),
            usuario.getIdPersona());

    int idFinca = usuario.getFinca() != null ? usuario.getFinca().getId() : 0;
    AuthenticationResponse response =
        new AuthenticationResponse(
            jwt,
            usuario.getIdPersona(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getTipoUsuario(),
            idFinca);

    return ResponseEntity.ok(response);
  }
}
