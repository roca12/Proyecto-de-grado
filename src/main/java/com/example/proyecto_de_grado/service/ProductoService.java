package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ProductoDTO;
import com.example.proyecto_de_grado.model.entity.InventarioProducto;
import com.example.proyecto_de_grado.model.entity.Producto;
import com.example.proyecto_de_grado.repository.InventarioProductoRepository;
import com.example.proyecto_de_grado.repository.ProductoRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de Productos. Incluye creación, consulta, actualización y eliminación.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class ProductoService {

  @Autowired private ProductoRepository productoRepo;

  @Autowired private InventarioProductoRepository inventarioRepo;

  /**
   * Crea un nuevo producto y genera su inventario inicial en cero.
   *
   * @param dto Datos del producto.
   * @return ProductoDTO con el ID generado.
   */
  @Transactional
  public ProductoDTO crearProducto(ProductoDTO dto) {
    // Mapeo DTO -> Entidad
    Producto p = new Producto();
    p.setNombre(dto.getNombre());
    p.setDescripcion(dto.getDescripcion());
    p.setUnidadMedida(dto.getUnidadMedida());
    // Guardar entidad
    p = productoRepo.save(p);
    // Crear inventario inicial
    InventarioProducto inv = new InventarioProducto();
    inv.setProducto(p);
    inv.setCantidad(BigDecimal.ZERO);
    inv.setFechaActualizacion(LocalDateTime.now());
    inventarioRepo.save(inv);
    // Devolver DTO con ID
    dto.setIdProducto(p.getIdProducto());
    return dto;
  }

  /**
   * Obtiene todos los productos.
   *
   * @return Lista de ProductoDTO.
   */
  public List<ProductoDTO> listarProductos() {
    return productoRepo.findAll().stream()
        .map(
            p -> {
              ProductoDTO dto = new ProductoDTO();
              dto.setIdProducto(p.getIdProducto());
              dto.setNombre(p.getNombre());
              dto.setDescripcion(p.getDescripcion());
              dto.setUnidadMedida(p.getUnidadMedida());
              return dto;
            })
        .collect(Collectors.toList());
  }

  /**
   * Elimina un producto por su ID.
   *
   * @param idProducto ID del producto.
   */
  @Transactional
  public void eliminarProducto(Integer idProducto) {
    productoRepo.deleteById(idProducto);
  }
}
