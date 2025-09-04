package com.aproafa.proyectodegrado.controller;

import com.aproafa.proyectodegrado.model.dto.PrecioProductoDTO;
import com.aproafa.proyectodegrado.service.PrecioProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controlador para gestionar los precios históricos de los productos. */
@RestController
@RequestMapping("/api/precios")
public class PrecioProductoController {

  @Autowired private PrecioProductoService precioProductoService;

  /**
   * Crear un nuevo precio para un producto, cerrando el precio anterior.
   *
   * @param dto Datos del nuevo precio.
   * @return Precio creado con su ID.
   */
  @PostMapping
  public ResponseEntity<PrecioProductoDTO> crearNuevoPrecio(@RequestBody PrecioProductoDTO dto) {
    PrecioProductoDTO precioRegistrado = precioProductoService.crearNuevoPrecio(dto);
    return new ResponseEntity<>(precioRegistrado, HttpStatus.CREATED);
  }

  /** Obtener todos los precios de productos registrados. */
  @GetMapping
  public ResponseEntity<List<PrecioProductoDTO>> obtenerTodos() {
    List<PrecioProductoDTO> precios = precioProductoService.obtenerTodas();
    return ResponseEntity.ok(precios);
  }
}
