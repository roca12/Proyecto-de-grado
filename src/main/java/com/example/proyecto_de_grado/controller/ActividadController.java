package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.ActividadDTO;
import com.example.proyecto_de_grado.repository.UsuarioRepository;
import com.example.proyecto_de_grado.service.ActividadService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar operaciones relacionadas con actividades.
 *
 * <p>Este controlador proporciona endpoints para crear, leer, actualizar y eliminar actividades,
 * así como para listar actividades por finca con validación de permisos.
 *
 * @author [Anderson Zuluaga]
 * @version 1.0
 * @since 2023
 */
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/actividades")
public class ActividadController {

  @Autowired private ActividadService actividadService;

  @Autowired private UsuarioRepository usuarioRepository;

  /**
   * Obtiene la lista de todas las actividades registradas en el sistema.
   *
   * <p>Este endpoint retorna todas las actividades sin importar la finca a la que pertenecen. Es
   * útil para los usuarios con rol de administrador que requieren una vista general de todas las
   * actividades del sistema.
   *
   * @return ResponseEntity con la lista de todas las actividades en formato DTO y estado HTTP 200
   *     (OK)
   */
  @GetMapping
  public ResponseEntity<List<ActividadDTO>> listarTodas() {
    return ResponseEntity.ok(actividadService.listarTodas());
  }

  /**
   * Crea una nueva actividad.
   *
   * @param actividadDTO DTO con los datos de la actividad a crear
   * @return ResponseEntity con la actividad creada y estado HTTP 200 (OK)
   */
  @PostMapping
  public ResponseEntity<ActividadDTO> crearActividad(@RequestBody ActividadDTO actividadDTO) {
    ActividadDTO creada = actividadService.crearActividad(actividadDTO);
    return ResponseEntity.ok(creada);
  }

  /**
   * Obtiene todas las actividades asociadas a una finca específica.
   *
   * <p>Realiza validación de permisos:
   *
   * <ul>
   *   <li>Los usuarios ADMIN pueden ver actividades de cualquier finca
   *   <li>Otros usuarios solo pueden ver actividades de su finca asignada
   * </ul>
   *
   * @param idFinca ID de la finca para filtrar actividades
   * @return ResponseEntity con la lista de actividades y estado HTTP 200 (OK), o estado 403
   *     (Forbidden) si el usuario no tiene permisos
   * @throws RuntimeException si el usuario autenticado no se encuentra en la base de datos
   */
  @GetMapping("/finca/{idFinca}")
  public ResponseEntity<List<ActividadDTO>> obtenerActividadesPorFinca(
      @PathVariable Integer idFinca) {
    // Obtener el usuario autenticado desde el contexto de seguridad
    /*
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      int userId = Integer.parseInt(auth.getName()); // Obtener el ID del usuario autenticado
      Usuario usuario =
              usuarioRepository
                      .findById(userId)
                      .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

      // Validar que el usuario no sea un ADMIN, y si no lo es, verificar que esté asociado a la finca
      // correcta
      if (!usuario.getTipoUsuario().equalsIgnoreCase("ADMIN")) {
        // Si el usuario no tiene finca asignada o la finca no coincide, devolver 403
        if (usuario.getFinca() == null || !usuario.getFinca().getId().equals(idFinca)) {
          return ResponseEntity.status(403).build(); // Forbidden
        }
      }
    */
    // Obtener las actividades de la finca solicitada
    List<ActividadDTO> actividades = actividadService.listarPorFinca(idFinca);
    return ResponseEntity.ok(actividades);
  }

  /**
   * Obtiene una actividad por su ID.
   *
   * @param id ID de la actividad a buscar
   * @return ResponseEntity con la actividad encontrada y estado HTTP 200 (OK), o estado 404 (Not
   *     Found) si no se encuentra la actividad
   */
  @GetMapping("/{id}")
  public ResponseEntity<ActividadDTO> obtenerActividadPorId(@PathVariable Integer id) {
    return actividadService
        .obtenerPorId(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Actualiza una actividad existente.
   *
   * @param id ID de la actividad a actualizar
   * @param dto DTO con los nuevos datos de la actividad
   * @return ResponseEntity con la actividad actualizada y estado HTTP 200 (OK)
   */
  @PutMapping("/{id}")
  public ResponseEntity<ActividadDTO> actualizarActividad(
      @PathVariable Integer id, @RequestBody ActividadDTO dto) {
    ActividadDTO actualizada = actividadService.actualizarActividad(id, dto);
    return ResponseEntity.ok(actualizada);
  }

  /**
   * Elimina una actividad existente.
   *
   * @param id ID de la actividad a eliminar
   * @return ResponseEntity con estado HTTP 204 (No Content) si la eliminación fue exitosa
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarActividad(@PathVariable Integer id) {
    actividadService.eliminarActividad(id);
    return ResponseEntity.noContent().build();
  }
}
