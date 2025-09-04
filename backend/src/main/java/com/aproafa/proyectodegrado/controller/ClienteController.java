package com.aproafa.proyectodegrado.controller;

import com.aproafa.proyectodegrado.model.dto.ClienteDTO;
import com.aproafa.proyectodegrado.service.ClienteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de clientes.
 *
 * <p>Proporciona operaciones CRUD (Crear, Leer, Actualizar, Eliminar) para la entidad Cliente.
 * Todos los endpoints requieren autenticación y están mapeados bajo la ruta base '/api/clientes'.
 *
 * @author [Anderson Zuluaga]
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

  private final ClienteService clienteService;

  /**
   * Crea un nuevo cliente en el sistema.
   *
   * @param clienteDTO Objeto DTO con los datos del cliente a crear
   * @return ResponseEntity con el cliente creado y estado HTTP 200 (OK)
   */
  @PostMapping
  public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO) {
    ClienteDTO nuevoCliente = clienteService.crearCliente(clienteDTO);
    return ResponseEntity.ok(nuevoCliente);
  }

  @GetMapping("/finca/{idFinca}")
  public ResponseEntity<List<ClienteDTO>> listarClientesPorFinca(@PathVariable Integer idFinca) {
    List<ClienteDTO> clientes = clienteService.listarClientesPorFinca(idFinca);
    return ResponseEntity.ok(clientes);
  }

  /**
   * Obtiene una lista de todos los clientes registrados en el sistema.
   *
   * @return ResponseEntity con la lista de clientes y estado HTTP 200 (OK)
   */
  @GetMapping
  public ResponseEntity<List<ClienteDTO>> listarClientes() {
    return ResponseEntity.ok(clienteService.listarClientes());
  }

  /**
   * Obtiene un cliente específico por su ID.
   *
   * @param id ID del cliente a buscar
   * @return ResponseEntity con el cliente encontrado y estado HTTP 200 (OK)
   */
  @GetMapping("/{id}")
  public ResponseEntity<ClienteDTO> obtenerClientePorId(@PathVariable Integer id) {
    return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
  }

  /**
   * Actualiza los datos de un cliente existente.
   *
   * @param id ID del cliente a actualizar
   * @param clienteDTO Objeto DTO con los nuevos datos del cliente
   * @return ResponseEntity con el cliente actualizado y estado HTTP 200 (OK)
   */
  @PutMapping("/{id}")
  public ResponseEntity<ClienteDTO> actualizarCliente(
      @PathVariable Integer id, @RequestBody ClienteDTO clienteDTO) {
    return ResponseEntity.ok(clienteService.actualizarCliente(id, clienteDTO));
  }

  /**
   * Elimina un cliente del sistema.
   *
   * @param id ID del cliente a eliminar
   * @return ResponseEntity con estado HTTP 204 (No Content) si la eliminación fue exitosa
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) {
    clienteService.eliminarCliente(id);
    return ResponseEntity.noContent().build();
  }
}
