package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.EmpleadoDTO;
import com.example.proyecto_de_grado.service.EmpleadoService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de empleados.
 *
 * <p>Proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la entidad Empleado.
 * Todos los endpoints requieren autenticación y están mapeados bajo la ruta base '/api/empleados'.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

  private final EmpleadoService empleadoService;

  /**
   * Crea un nuevo empleado en el sistema.
   *
   * @param empleadoDTO Objeto DTO con los datos del empleado a crear
   * @return ResponseEntity con el empleado creado y estado HTTP 200 (OK)
   */
  @PostMapping
  public ResponseEntity<EmpleadoDTO> crearEmpleado(@RequestBody EmpleadoDTO empleadoDTO) {
    EmpleadoDTO nuevoEmpleado = empleadoService.crearEmpleado(empleadoDTO);
    return ResponseEntity.ok(nuevoEmpleado);
  }

  /**
   * Obtiene una lista de todos los empleados registrados en el sistema.
   *
   * @return ResponseEntity con la lista de empleados y estado HTTP 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<EmpleadoDTO>> listarEmpleados() {
    return ResponseEntity.ok(empleadoService.listarEmpleados());
  }

  /**
   * Obtiene un empleado específico por su ID.
   *
   * @param id ID del empleado a buscar
   * @return ResponseEntity con el empleado encontrado y estado HTTP 200 (OK)
   */
  @GetMapping("/{id}")
  public ResponseEntity<EmpleadoDTO> obtenerEmpleadoPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(empleadoService.obtenerEmpleadoPorId(id));
  }

  /**
   * Actualiza los datos de un empleado existente.
   *
   * @param id ID del empleado a actualizar
   * @param empleadoDTO Objeto DTO con los nuevos datos del empleado
   * @return ResponseEntity con el empleado actualizado y estado HTTP 200 (OK)
   */
  @PutMapping("/{id}")
  public ResponseEntity<EmpleadoDTO> actualizarEmpleado(
      @PathVariable Integer id, @RequestBody EmpleadoDTO empleadoDTO) {
    return ResponseEntity.ok(empleadoService.actualizarEmpleado(id, empleadoDTO));
  }

  /**
   * Elimina un empleado del sistema.
   *
   * @param id ID del empleado a eliminar
   * @return ResponseEntity con estado HTTP 204 (No Content) si la eliminación fue exitosa
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarEmpleado(@PathVariable Integer id) {
    empleadoService.eliminarEmpleado(id);
    return ResponseEntity.noContent().build();
  }
}
