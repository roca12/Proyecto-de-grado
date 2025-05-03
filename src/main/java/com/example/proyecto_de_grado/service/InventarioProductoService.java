package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.InventarioProductoDTO;
import com.example.proyecto_de_grado.model.entity.InventarioProducto;
import com.example.proyecto_de_grado.repository.ProductoRepository;
import com.example.proyecto_de_grado.repository.InventarioProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Servicio para la gestión del inventario de productos.
 * Este servicio incluye métodos para consultar y actualizar el inventario de productos.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class InventarioProductoService {

    @Autowired
    private InventarioProductoRepository inventarioRepo;

    @Autowired
    private ProductoRepository productoRepo;

    /**
     * Obtiene el inventario de un producto.
     * Este método consulta el inventario de un producto específico a partir de su ID.
     *
     * @param idProducto ID del producto cuyo inventario se desea consultar.
     * @return DTO con los detalles del inventario del producto, incluyendo cantidad y fecha de actualización.
     */
    public InventarioProductoDTO obtenerInventario(Integer idProducto) {
        InventarioProducto inv = inventarioRepo.findByProducto_IdProducto(idProducto)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        InventarioProductoDTO dto = new InventarioProductoDTO();
        dto.setIdInventario(inv.getIdInventario());
        dto.setIdProducto(inv.getProducto().getIdProducto());
        dto.setCantidad(inv.getCantidad());
        dto.setFechaActualizacion(inv.getFechaActualizacion());

        return dto;
    }

    /**
     * Actualiza la cantidad de inventario de un producto.
     * Este método permite incrementar o decrementar la cantidad de un producto en el inventario.
     * Se realiza una operación transaccional para asegurar que la actualización se haga de forma consistente.
     *
     * @param idProducto ID del producto cuyo inventario se desea actualizar.
     * @param cantidadDelta Incremento o decremento a aplicar a la cantidad de inventario.
     */
    @Transactional
    public void actualizarInventario(Integer idProducto, BigDecimal cantidadDelta) {
        InventarioProducto inv = inventarioRepo.findByProducto_IdProducto(idProducto)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado"));

        inv.setCantidad(inv.getCantidad().add(cantidadDelta)); // Actualiza la cantidad
        inv.setFechaActualizacion(LocalDateTime.now()); // Registra la fecha de actualización
        inventarioRepo.save(inv); // Guarda los cambios en la base de datos
    }
}
