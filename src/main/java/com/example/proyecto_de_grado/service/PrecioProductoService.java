package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.PrecioProductoDTO;
import com.example.proyecto_de_grado.model.entity.PrecioProducto;
import com.example.proyecto_de_grado.model.entity.Producto;
import com.example.proyecto_de_grado.repository.PrecioProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de los precios de los productos.
 * Incluye la creación de nuevos precios, cierre de precios anteriores y consulta de precios registrados.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Service
public class PrecioProductoService {

    @Autowired
    private PrecioProductoRepository precioRepo;

    /**
     * Crea un nuevo precio para un producto y cierra el precio anterior.
     * El precio anterior se marca como cerrado, estableciendo su fecha de fin en el día anterior al inicio del nuevo precio.
     *
     * @param dto Datos del nuevo precio.
     * @return DTO con el ID del nuevo precio creado.
     */
    @Transactional
    public PrecioProductoDTO crearNuevoPrecio(PrecioProductoDTO dto) {
        // Cierra el precio vigente (fechaFin = día anterior)
        precioRepo.findByProducto_IdProductoAndFechaFinIsNull(dto.getIdProducto())
                .ifPresent(prev -> {
                    prev.setFechaFin(dto.getFechaInicio().minusDays(1)); // Marca el precio anterior como cerrado
                    precioRepo.save(prev); // Guarda los cambios del precio anterior
                });

        // Crea un nuevo precio
        PrecioProducto nuevo = new PrecioProducto();
        nuevo.setProducto(new Producto(dto.getIdProducto())); // Establece el producto
        nuevo.setFechaInicio(dto.getFechaInicio()); // Establece la fecha de inicio
        nuevo.setFechaFin(dto.getFechaFin()); // Establece la fecha de fin
        nuevo.setPrecio(dto.getPrecio()); // Establece el precio
        nuevo = precioRepo.save(nuevo); // Guarda el nuevo precio
        dto.setIdPrecio(nuevo.getIdPrecio()); // Establece el ID generado para el nuevo precio
        return dto;
    }

    /**
     * Obtiene todos los precios registrados.
     *
     * @return Lista de DTOs con todos los precios de productos.
     */
    public List<PrecioProductoDTO> obtenerTodas() {
        return precioRepo.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Convierte una entidad PrecioProducto a su DTO correspondiente.
     *
     * @param precioProducto Entidad de PrecioProducto.
     * @return DTO correspondiente de PrecioProducto.
     */
    private PrecioProductoDTO toDTO(PrecioProducto precioProducto) {
        PrecioProductoDTO dto = new PrecioProductoDTO();
        dto.setIdPrecio(precioProducto.getIdPrecio());
        dto.setIdProducto(precioProducto.getProducto().getIdProducto());
        dto.setFechaInicio(precioProducto.getFechaInicio());
        dto.setFechaFin(precioProducto.getFechaFin());
        dto.setPrecio(precioProducto.getPrecio());
        return dto;
    }
}
