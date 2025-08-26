package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.ProductoDTO;
import com.example.proyecto_de_grado.model.entity.InventarioProducto;
import com.example.proyecto_de_grado.model.entity.Producto;
import com.example.proyecto_de_grado.repository.FincaRepository;
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
  @Autowired
  private FincaRepository fincaRepo;

    @Transactional
    public ProductoDTO crearProducto(ProductoDTO dto) {
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setUnidadMedida(dto.getUnidadMedida());

        // asignar finca
        if (dto.getIdFinca() != null) {
            p.setFinca(fincaRepo.findById(dto.getIdFinca())
                    .orElseThrow(() -> new RuntimeException("Finca no encontrada")));
        }

        p = productoRepo.save(p);

        InventarioProducto inv = new InventarioProducto();
        inv.setProducto(p);
        inv.setCantidad(BigDecimal.ZERO);
        inv.setFechaActualizacion(LocalDateTime.now());
        inventarioRepo.save(inv);

        dto.setIdProducto(p.getIdProducto());
        return dto;
    }

    public List<ProductoDTO> listarPorFinca(Integer idFinca) {
        return productoRepo.findByFincaId(idFinca).stream()
                .map(p -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setIdProducto(p.getIdProducto());
                    dto.setNombre(p.getNombre());
                    dto.setDescripcion(p.getDescripcion());
                    dto.setUnidadMedida(p.getUnidadMedida());
                    dto.setIdFinca(p.getFinca().getId());
                    return dto;
                }).collect(Collectors.toList());
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
              dto.setIdFinca(p.getFinca().getId());
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
