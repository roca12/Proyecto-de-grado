package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.entity.CompraInsumo;
import com.example.proyecto_de_grado.service.CompraInsumoService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la gestión de compras de insumos.
 *
 * <p>Proporciona endpoints para realizar operaciones CRUD sobre compras de insumos, incluyendo
 * consultas por insumo y proveedor, así como el registro de nuevas compras que actualizan
 * automáticamente el stock disponible.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@RestController
@RequestMapping("/api/compra-insumos")
public class CompraInsumoController {

  @Autowired private CompraInsumoService compraInsumoService;

  /**
   * Obtiene todas las compras de insumos registradas en el sistema.
   *
   * @return Lista de todas las compras de insumos
   */
  @GetMapping
  public List<CompraInsumo> getAllCompras() {
    return compraInsumoService.getAllCompras();
  }

  /**
   * Obtiene todas las compras asociadas a un insumo específico.
   *
   * @param id ID del insumo para filtrar las compras
   * @return Lista de compras del insumo especificado
   */
  @GetMapping("/insumo/{id}")
  public List<CompraInsumo> getComprasByInsumo(@PathVariable int id) {
    return compraInsumoService.getComprasByInsumo(id);
  }

  /**
   * Obtiene todas las compras asociadas a un proveedor específico.
   *
   * @param id ID del proveedor para filtrar las compras
   * @return Lista de compras del proveedor especificado
   */
  @GetMapping("/proveedor/{id}")
  public List<CompraInsumo> getComprasByProveedor(@PathVariable int id) {
    return compraInsumoService.getComprasByProveedor(id);
  }

  /**
   * Registra una nueva compra de insumos y actualiza el stock disponible.
   *
   * @param compra Objeto CompraInsumo con los datos de la compra a registrar
   * @return ResponseEntity con mensaje de éxito (200 OK) o mensaje de error (400 Bad Request)
   * @throws IllegalArgumentException si ocurre algún error en la validación de la compra
   */
  @PostMapping
  public ResponseEntity<?> createCompra(
      @Valid @RequestBody CompraInsumo compra, BindingResult result) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
          .body(
              result.getAllErrors().stream()
                  .map(DefaultMessageSourceResolvable::getDefaultMessage)
                  .collect(Collectors.joining(", ")));
    }

    try {
      compraInsumoService.saveCompra(compra);
      return ResponseEntity.ok("Compra registrada y stock actualizado.");
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }
}
