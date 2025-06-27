package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.InventarioProductoDTO;
import com.example.proyecto_de_grado.service.InventarioProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controlador REST para gestionar el inventario de productos.
 * Proporciona endpoints para obtener y actualizar la información del inventario de un producto.
 * <p>Autor: Anderson Zuluaga</p>
 */
@RestController
@RequestMapping("/api/inventario_producto")
public class InventarioProductoController {

    @Autowired
    private InventarioProductoService inventarioService;

    /**
     * Obtiene el inventario de un producto específico.
     *
     * @param idProducto ID del producto cuyo inventario se desea consultar.
     * @return Respuesta con el DTO del inventario del producto y código de estado HTTP 200 (OK).
     */
    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<InventarioProductoDTO> obtenerInventario(
            @PathVariable Integer idProducto) {
        // Llama al servicio para obtener el inventario del producto por su ID
        InventarioProductoDTO inventario = inventarioService.obtenerInventario(idProducto);
        return new ResponseEntity<>(inventario, HttpStatus.OK); // Devuelve el inventario con código de estado 200
    }

    /**
     * Actualiza el inventario de un producto incrementando o decrementando su cantidad.
     *
     * @param idProducto ID del producto cuyo inventario se actualizará.
     * @param cantidadDelta Cantidad a incrementar (valor positivo) o decrementar (valor negativo).
     * @return Respuesta sin contenido y código de estado HTTP 204 (NO CONTENT).
     */
    @PatchMapping("/producto/{idProducto}")
    public ResponseEntity<Void> actualizarInventario(
            @PathVariable Integer idProducto,
            @RequestParam BigDecimal cantidadDelta) {
        // Llama al servicio para actualizar el inventario del producto con el cambio especificado
        inventarioService.actualizarInventario(idProducto, cantidadDelta);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // Respuesta sin contenido (204)
    }
}
