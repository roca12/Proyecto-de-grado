package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.ProduccionDTO;
import com.example.proyecto_de_grado.model.dto.UsoInsumoProduccionDTO;
import com.example.proyecto_de_grado.service.ProduccionService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar las operaciones relacionadas con la producción (siembra y cosecha).
 * Proporciona los endpoints necesarios para crear, cosechar, listar y eliminar producciones.
 *
 * @author Anderson Zuluaga
 */
@RestController
@RequestMapping("/produccion")
public class ProduccionController {

  @Autowired private ProduccionService produccionService;

  /**
   * Crea una nueva producción (siembra).
   *
   * <p>Este método recibe los datos de producción a través de un DTO, y utiliza el servicio de
   * producción para crear la nueva producción en la base de datos.
   *
   * @param dto Datos de producción a crear. Contiene la información necesaria para la creación.
   * @return ResponseEntity con el DTO de la producción creada y el estado HTTP 201 (CREATED).
   */
  @PostMapping
  public ResponseEntity<ProduccionDTO> crearProduccion(@RequestBody ProduccionDTO dto) {
    ProduccionDTO createdProduccion = produccionService.crearProduccion(dto);
    return new ResponseEntity<>(createdProduccion, HttpStatus.CREATED);
  }

  @GetMapping("/finca/{idFinca}")
  public ResponseEntity<List<ProduccionDTO>> listarProduccionesPorFinca(
      @PathVariable Integer idFinca) {
    List<ProduccionDTO> proveedores = produccionService.listarPorFinca(idFinca);
    return ResponseEntity.ok(proveedores);
  }

  /**
   * Marca una producción como cosechada.
   *
   * <p>Este método recibe el ID de la producción, la cantidad cosechada y la fecha de cosecha.
   * Luego, utiliza el servicio de producción para actualizar el estado de la producción como
   * cosechada.
   *
   * @param id ID de la producción a cosechar.
   * @param cantidadCosechada Cantidad cosechada de la producción.
   * @param fechaCosecha Fecha en que se realiza la cosecha.
   * @return ResponseEntity con el estado HTTP 200 (OK) si la operación es exitosa.
   */
  @PutMapping("/{id}/cosechar")
  public ResponseEntity<Void> cosecharProduccion(
      @PathVariable Integer id,
      @RequestParam BigDecimal cantidadCosechada,
      @RequestParam LocalDate fechaCosecha) {
    produccionService.cosechar(id, cantidadCosechada, fechaCosecha);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Lista todas las producciones existentes.
   *
   * <p>Este método obtiene todas las producciones almacenadas en el sistema mediante el servicio de
   * producción.
   *
   * @return ResponseEntity con una lista de DTOs de producciones y el estado HTTP 200 (OK).
   */
  @GetMapping
  public ResponseEntity<List<ProduccionDTO>> listarProducciones() {
    List<ProduccionDTO> producciones = produccionService.listarProducciones();
    return ResponseEntity.ok(producciones);
  }

  /**
   * Obtiene los detalles de una producción específica por su ID.
   *
   * <p>Este método obtiene una producción mediante su ID y la devuelve en formato DTO.
   *
   * @param id ID de la producción que se desea obtener.
   * @return ResponseEntity con el DTO de la producción y el estado HTTP 200 (OK).
   */
  @GetMapping("/{id}")
  public ResponseEntity<ProduccionDTO> obtenerProduccion(@PathVariable Integer id) {
    ProduccionDTO produccion = produccionService.obtenerProduccionPorId(id);
    return ResponseEntity.ok(produccion);
  }

  /**
   * Elimina una producción existente por su ID.
   *
   * <p>Este método elimina una producción del sistema mediante su ID.
   *
   * @param id ID de la producción a eliminar.
   * @return ResponseEntity con estado HTTP 204 (NO_CONTENT) si la eliminación fue exitosa.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarProduccion(@PathVariable Integer id) {
    produccionService.eliminarProduccion(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{id}/insumos")
  public ResponseEntity<List<UsoInsumoProduccionDTO>> obtenerInsumosProduccion(
      @PathVariable Integer id) {
    List<UsoInsumoProduccionDTO> insumos = produccionService.obtenerInsumosPorProduccion(id);
    return ResponseEntity.ok(insumos);
  }
}
