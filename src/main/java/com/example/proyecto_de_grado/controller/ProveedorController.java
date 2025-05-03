package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.ProveedorDTO;
import com.example.proyecto_de_grado.service.ProveedorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de proveedores.
 *
 * <p>Proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la entidad Proveedor.
 * Todos los endpoints están mapeados bajo la ruta base '/api/proveedores'.
 *
 * <p>Autor: Anderson Zuluaga
 */
@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

  private final ProveedorService proveedorService;

  /**
   * Crea un nuevo proveedor en el sistema.
   *
   * @param proveedorDTO Objeto DTO con los datos del proveedor a crear
   * @return ResponseEntity con el proveedor creado y estado HTTP 200 (OK)
   */
  @PostMapping
  public ResponseEntity<ProveedorDTO> crearProveedor(@RequestBody ProveedorDTO proveedorDTO) {
    ProveedorDTO nuevoProveedor = proveedorService.crearProveedor(proveedorDTO);
    return ResponseEntity.ok(nuevoProveedor);
  }

  /**
   * Obtiene una lista de todos los proveedores registrados en el sistema.
   *
   * @return ResponseEntity con la lista de proveedores y estado HTTP 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<ProveedorDTO>> listarProveedores() {
    return ResponseEntity.ok(proveedorService.listarProveedores());
  }

  /**
   * Obtiene un proveedor específico por su ID.
   *
   * @param id ID del proveedor a buscar
   * @return ResponseEntity con el proveedor encontrado y estado HTTP 200 (OK)
   */
  @GetMapping("/{id}")
  public ResponseEntity<ProveedorDTO> obtenerProveedorPorId(@PathVariable Integer id) {
    return ResponseEntity.ok(proveedorService.obtenerProveedorPorId(id));
  }

  /**
   * Actualiza los datos de un proveedor existente.
   *
   * @param id ID del proveedor a actualizar
   * @param proveedorDTO Objeto DTO con los nuevos datos del proveedor
   * @return ResponseEntity con el proveedor actualizado y estado HTTP 200 (OK)
   */
  @PutMapping("/{id}")
  public ResponseEntity<ProveedorDTO> actualizarProveedor(
      @PathVariable Integer id, @RequestBody ProveedorDTO proveedorDTO) {
    return ResponseEntity.ok(proveedorService.actualizarProveedor(id, proveedorDTO));
  }

  /**
   * Elimina un proveedor del sistema.
   *
   * @param id ID del proveedor a eliminar
   * @return ResponseEntity con estado HTTP 204 (No Content) si la eliminación fue exitosa
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarProveedor(@PathVariable Integer id) {
    proveedorService.eliminarProveedor(id);
    return ResponseEntity.noContent().build();
  }
}
