package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.InsumoDTO;
import com.example.proyecto_de_grado.model.entity.Finca;
import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.model.entity.Proveedor;
import com.example.proyecto_de_grado.repository.ProveedorRepository;
import com.example.proyecto_de_grado.repository.FincaRepository;
import com.example.proyecto_de_grado.service.InsumoService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador REST para la gestión de insumos agrícolas.
 *
 * <p>Permite operaciones CRUD sobre los insumos, como creación, lectura, actualización,
 * eliminación, consulta de bajo stock y registro de uso.
 *
 * @author Anderson Zuluaga
 */
@RestController
@RequestMapping("/insumos")
public class InsumoController {

  @Autowired private InsumoService insumoService;

  @Autowired private ProveedorRepository proveedorRepository;
  @Autowired
  private FincaRepository fincaRepository;

  /**
   * Obtiene todos los insumos registrados.
   *
   * @return lista de insumos
   */
  @GetMapping
  public List<Insumo> getAllInsumos() {
    return insumoService.getAllInsumos();
  }

  /**
   * Obtiene un insumo por su ID.
   *
   * @param id identificador del insumo
   * @return respuesta con el insumo encontrado o estado 404 si no existe
   */
  @GetMapping("/{id}")
  public ResponseEntity<Insumo> getInsumoById(@PathVariable int id) {
    Optional<Insumo> insumo = insumoService.getInsumoById(id);
    return insumo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/finca/{idFinca}")
  public ResponseEntity<List<Insumo>> listarInsumosPorFinca(@PathVariable Integer idFinca) {
    List<Insumo> insumos = insumoService.listarPorFinca(idFinca);
    return ResponseEntity.ok(insumos);
  }


  /**
   * Consulta los insumos cuyo stock es inferior a un valor límite.
   *
   * @param limite cantidad mínima aceptable
   * @return lista de insumos con stock bajo
   */
  @GetMapping("/bajo-stock/{limite}")
  public List<Insumo> getInsumosBajosStock(@PathVariable BigDecimal limite) {
    return insumoService.getInsumosBajosStock(limite);
  }

  /**
   * Crea un nuevo insumo a partir de un DTO.
   *
   * @param dto datos del insumo a crear
   * @return respuesta con el insumo creado o mensaje de error
   */
  @PostMapping
  public ResponseEntity<?> createInsumo(@RequestBody InsumoDTO dto) {
    if (dto.getIdProveedor() == null) {
      return ResponseEntity.badRequest().body("El ID del proveedor es obligatorio.");
    }

    if (dto.getIdFinca() == null) {
      return ResponseEntity.badRequest().body("El ID de la finca es obligatorio.");
    }

    Proveedor proveedor =
            proveedorRepository
                    .findById(dto.getIdProveedor())
                    .orElseThrow(
                            () ->
                                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado"));

    // Buscar la finca
    Finca finca = fincaRepository.findById(dto.getIdFinca())
            .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Finca no encontrada"));

    Insumo insumo = new Insumo();
    insumo.setNombre(dto.getNombre());
    insumo.setDescripcion(dto.getDescripcion());
    insumo.setUnidadMedida(dto.getUnidadMedida());
    insumo.setCantidadDisponible(dto.getCantidadDisponible());
    insumo.setProveedor(proveedor);
    insumo.setFinca(finca);

    return ResponseEntity.ok(insumoService.saveInsumo(insumo));
  }
  /**
   * Registra el uso de una cantidad de insumo y actualiza su stock.
   *
   * @param id ID del insumo
   * @param cantidad cantidad utilizada
   * @return mensaje de confirmación
   */
  @PostMapping("/uso/{id}/{cantidad}")
  public ResponseEntity<String> registrarUsoInsumo(
      @PathVariable int id, @PathVariable BigDecimal cantidad) {
    insumoService.registrarUsoInsumo(id, cantidad);
    return ResponseEntity.ok("Uso registrado y stock actualizado.");
  }

  /**
   * Obtiene el historial de movimientos de un insumo.
   *
   * @param id ID del insumo
   * @return lista de movimientos (entradas/salidas) del insumo
   */
  @GetMapping("/historial/{id}")
  public List<HistorialInsumo> getHistorialInsumo(@PathVariable int id) {
    return insumoService.getHistorialInsumo(id);
  }

  /**
   * Elimina un insumo por su ID.
   *
   * @param id ID del insumo a eliminar
   * @return mensaje de confirmación
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteInsumo(@PathVariable int id) {
    insumoService.deleteInsumo(id);
    return ResponseEntity.ok("Insumo eliminado correctamente");
  }

  /**
   * Actualiza los datos de un insumo existente.
   *
   * @param id ID del insumo a actualizar
   * @param dto datos actualizados del insumo
   * @return respuesta con el insumo actualizado o error si no existe
   */
  @PutMapping("/{id}")
  public ResponseEntity<?> updateInsumo(@PathVariable int id, @RequestBody InsumoDTO dto) {
    Optional<Insumo> optionalInsumo = insumoService.getInsumoById(id);

    if (optionalInsumo.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    Proveedor proveedor =
        proveedorRepository
            .findById(dto.getIdProveedor())
            .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

    Insumo insumo = optionalInsumo.get();
    insumo.setNombre(dto.getNombre());
    insumo.setDescripcion(dto.getDescripcion());
    insumo.setUnidadMedida(dto.getUnidadMedida());
    insumo.setCantidadDisponible(dto.getCantidadDisponible());
    insumo.setProveedor(proveedor);

    return ResponseEntity.ok(insumoService.saveInsumo(insumo));
  }
}
