package com.aproafa.proyectodegrado.controller;

import com.aproafa.proyectodegrado.model.dto.CalidadProductoDTO;
import com.aproafa.proyectodegrado.service.CalidadProductoService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Controlador para gestionar la calidad de los productos en la producción. */
@RestController
@RequestMapping("/api/calidades")
public class CalidadProductoController {

  @Autowired private CalidadProductoService calidadProductoService;

  /**
   * Registrar la calidad de una producción.
   *
   * @param dto Datos de la calidad a registrar.
   * @return Calidad registrada con su ID.
   */
  @PostMapping
  public ResponseEntity<CalidadProductoDTO> registrarCalidad(@RequestBody CalidadProductoDTO dto) {
    CalidadProductoDTO calidadRegistrada = calidadProductoService.registrarCalidad(dto);
    return new ResponseEntity<>(calidadRegistrada, HttpStatus.CREATED);
  }

  /**
   * Obtener las calidades.
   *
   * @return Obtiene todasd las calidades.
   */
  @GetMapping
  public ResponseEntity<List<CalidadProductoDTO>> obtenerTodas() {
    return ResponseEntity.ok(calidadProductoService.obtenerTodas());
  }
}
