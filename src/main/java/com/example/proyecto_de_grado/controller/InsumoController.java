package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.InsumoDTO;
import com.example.proyecto_de_grado.model.entity.HistorialInsumo;
import com.example.proyecto_de_grado.model.entity.Insumo;
import com.example.proyecto_de_grado.model.entity.Proveedor;
import com.example.proyecto_de_grado.repository.ProveedorRepository;
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
 * <p>Proporciona operaciones para el manejo de insumos.
 */
@RestController
@RequestMapping("/api/insumos")
public class InsumoController {

  @Autowired private InsumoService insumoService;
  @Autowired private ProveedorRepository proveedorRepository;

  @GetMapping
  public List<Insumo> getAllInsumos() {
    return insumoService.getAllInsumos();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Insumo> getInsumoById(@PathVariable int id) {
    Optional<Insumo> insumo = insumoService.getInsumoById(id);
    return insumo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping("/bajo-stock/{limite}")
  public List<Insumo> getInsumosBajosStock(@PathVariable BigDecimal limite) {
    return insumoService.getInsumosBajosStock(limite);
  }

  @PostMapping
  public ResponseEntity<?> createInsumo(@RequestBody InsumoDTO dto) {
    if (dto.getIdProveedor() == null) {
      return ResponseEntity
              .badRequest()
              .body("El ID del proveedor es obligatorio.");
    }

    Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Proveedor no encontrado"));

    // Crear entidad desde el DTO
    Insumo insumo = new Insumo();
    insumo.setNombre(dto.getNombre());
    insumo.setDescripcion(dto.getDescripcion());
    insumo.setUnidadMedida(dto.getUnidadMedida());
    insumo.setCantidadDisponible(dto.getCantidadDisponible());
    insumo.setProveedor(proveedor);

    return ResponseEntity.ok(insumoService.saveInsumo(insumo));
  }

  @PostMapping("/uso/{id}/{cantidad}")
  public ResponseEntity<String> registrarUsoInsumo(
          @PathVariable int id, @PathVariable BigDecimal cantidad) {
    insumoService.registrarUsoInsumo(id, cantidad);
    return ResponseEntity.ok("Uso registrado y stock actualizado.");
  }

  @GetMapping("/historial/{id}")
  public List<HistorialInsumo> getHistorialInsumo(@PathVariable int id) {
    return insumoService.getHistorialInsumo(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<String> deleteInsumo(@PathVariable int id) {
    insumoService.deleteInsumo(id);
    return ResponseEntity.ok("Insumo eliminado correctamente");
  }
  @PutMapping("/{id}")
  public ResponseEntity<?> updateInsumo(@PathVariable int id, @RequestBody InsumoDTO dto) {
    Optional<Insumo> optionalInsumo = insumoService.getInsumoById(id);

    if (optionalInsumo.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    // Validar proveedor
    Proveedor proveedor = proveedorRepository.findById(dto.getIdProveedor())
            .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

    // Actualizar campos
    Insumo insumo = optionalInsumo.get();
    insumo.setNombre(dto.getNombre());
    insumo.setDescripcion(dto.getDescripcion());
    insumo.setUnidadMedida(dto.getUnidadMedida());
    insumo.setCantidadDisponible(dto.getCantidadDisponible());
    insumo.setProveedor(proveedor);

    return ResponseEntity.ok(insumoService.saveInsumo(insumo));
  }



}
