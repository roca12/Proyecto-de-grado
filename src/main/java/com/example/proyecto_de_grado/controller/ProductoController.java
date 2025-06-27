package com.example.proyecto_de_grado.controller;

import com.example.proyecto_de_grado.model.dto.ProductoDTO;
import com.example.proyecto_de_grado.service.ProductoService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para gestionar las operaciones relacionadas con los productos. Proporciona los
 * endpoints necesarios para crear, listar y eliminar productos.
 *
 * @author Anderson Zuluaga
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

  @Autowired private ProductoService productoService;

  /**
   * Crea un nuevo producto.
   *
   * <p>Este método recibe los datos del producto a través de un DTO, y utiliza el servicio de
   * producto para crear el nuevo producto en la base de datos.
   *
   * @param productoDTO Datos del producto a crear. Contiene la información necesaria para la
   *     creación.
   * @return ResponseEntity con el DTO del producto creado y el estado HTTP 201 (CREATED).
   */
  @PostMapping
  public ResponseEntity<ProductoDTO> crearProducto(@RequestBody ProductoDTO productoDTO) {
    ProductoDTO nuevoProducto = productoService.crearProducto(productoDTO);
    return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
  }

  /**
   * Obtiene todos los productos almacenados.
   *
   * <p>Este método obtiene todos los productos almacenados en el sistema mediante el servicio de
   * productos.
   *
   * @return ResponseEntity con una lista de DTOs de productos y el estado HTTP 200 (OK).
   */
  @GetMapping
  public ResponseEntity<List<ProductoDTO>> listarProductos() {
    List<ProductoDTO> productos = productoService.listarProductos();
    return new ResponseEntity<>(productos, HttpStatus.OK);
  }

  /**
   * Elimina un producto por su ID.
   *
   * <p>Este método elimina un producto específico mediante su ID utilizando el servicio de
   * productos.
   *
   * @param id ID del producto a eliminar.
   * @return ResponseEntity con el estado HTTP 204 (NO CONTENT) si la eliminación es exitosa.
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminarProducto(@PathVariable Integer id) {
    productoService.eliminarProducto(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
