package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.InventarioProductoDTO;
import com.example.proyecto_de_grado.service.InventarioProductoService;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario_producto")
public class InventarioProductoController {

  @Autowired private InventarioProductoService inventarioService;

  /**
   * Obtiene el inventario de un producto
   *
   * @param idProducto ID del producto
   * @return DTO con la información del inventario
   */
  @GetMapping("/producto/{idProducto}")
  public ResponseEntity<InventarioProductoDTO> obtenerInventario(@PathVariable Integer idProducto) {
    InventarioProductoDTO inventario = inventarioService.obtenerInventario(idProducto);
    return new ResponseEntity<>(inventario, HttpStatus.OK);
  }

  /**
   * Actualiza el inventario de un producto
   *
   * @param idProducto ID del producto
   * @param cantidadDelta Cantidad a incrementar/decrementar (valor positivo o negativo)
   * @return Respuesta sin contenido
   */
  @PatchMapping("/producto/{idProducto}")
  public ResponseEntity<Void> actualizarInventario(
      @PathVariable Integer idProducto, @RequestParam BigDecimal cantidadDelta) {
    inventarioService.actualizarInventario(idProducto, cantidadDelta);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
