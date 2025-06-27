package com.example.proyecto_de_grado.service;

import com.example.proyecto_de_grado.model.dto.DetalleVentaDTO;
import com.example.proyecto_de_grado.model.dto.VentaDTO;
import com.example.proyecto_de_grado.model.entity.MetodoPago;
import com.example.proyecto_de_grado.model.entity.Venta;
import com.example.proyecto_de_grado.model.entity.DetalleVenta;
import com.example.proyecto_de_grado.repository.VentaRepository;
import com.example.proyecto_de_grado.repository.DetalleVentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de ventas y sus detalles.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Service
@Transactional
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;

    @Autowired
    public VentaService(VentaRepository ventaRepository,
                        DetalleVentaRepository detalleVentaRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
    }

    /**
     * Obtiene una venta por su identificador único.
     */
    @Transactional(readOnly = true)
    public VentaDTO obtenerVentaPorId(Integer idVenta) {
        Optional<Venta> ventaOptional = ventaRepository.findById(idVenta);
        return ventaOptional.map(this::convertirAVentaDTO).orElse(null);
    }

    /**
     * Obtiene todos los detalles asociados a una venta específica.
     */
    @Transactional(readOnly = true)
    public List<DetalleVentaDTO> obtenerDetallesDeVenta(Integer idVenta) {
        List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_IdVenta(idVenta);
        return detalles.stream()
                .map(this::convertirADetalleVentaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las ventas registradas en el sistema.
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> obtenerTodasLasVentas() {
        List<Venta> ventas = ventaRepository.findAll();
        return ventas.stream()
                .map(this::convertirAVentaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Guarda una nueva venta en el sistema.
     */
    public VentaDTO guardarVenta(VentaDTO ventaDTO) {
        if (ventaDTO.getMetodoPago() == null || ventaDTO.getMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }

        Venta venta = convertirAVentaEntity(ventaDTO);
        venta.setFechaVenta(LocalDateTime.now());

        if (venta.getTotal() == null) {
            venta.setTotal(BigDecimal.ZERO);
        }

        Venta ventaGuardada = ventaRepository.save(venta);
        return convertirAVentaDTO(ventaGuardada);
    }
    /**
     * Guarda una nueva venta junto con sus detalles de forma transaccional.
     */
    public VentaDTO guardarVentaConDetalles(VentaDTO ventaDTO, List<DetalleVentaDTO> detallesDTO) {
        if (ventaDTO.getMetodoPago() == null || ventaDTO.getMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }

        // Validar que hay detalles
        if (detallesDTO == null || detallesDTO.isEmpty()) {
            throw new IllegalArgumentException("Una venta debe tener al menos un detalle");
        }

        BigDecimal totalCalculado = BigDecimal.ZERO;
        for (DetalleVentaDTO detalleDTO : detallesDTO) {

            if (detalleDTO.getIdProducto() == null || detalleDTO.getCantidad() == null ||
                    detalleDTO.getPrecioUnitario() == null) {
                throw new IllegalArgumentException("Todos los campos del detalle son obligatorios");
            }

            BigDecimal subtotal = detalleDTO.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
            totalCalculado = totalCalculado.add(subtotal);
        }


        ventaDTO.setTotal(totalCalculado);

        VentaDTO ventaGuardada = guardarVenta(ventaDTO);

        if (ventaGuardada == null || ventaGuardada.getIdVenta() == null) {
            throw new RuntimeException("Error al guardar la venta");
        }


        for (DetalleVentaDTO detalleDTO : detallesDTO) {
            detalleDTO.setIdVenta(ventaGuardada.getIdVenta());
            detalleDTO.setSubtotal(null);

            DetalleVenta detalleEntity = convertirADetalleVentaEntity(detalleDTO);

            if (detalleEntity.getIdVenta() == null) {
                throw new RuntimeException("Error: idVenta es null en el detalle");
            }

            detalleVentaRepository.save(detalleEntity);
        }

        return ventaGuardada;
    }
    /**
     * Actualiza una venta existente.
     */
    public VentaDTO actualizarVenta(Integer idVenta, VentaDTO ventaDTO) {
        Optional<Venta> ventaOptional = ventaRepository.findById(idVenta);
        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            actualizarCamposVenta(venta, ventaDTO);
            Venta ventaActualizada = ventaRepository.save(venta);
            return convertirAVentaDTO(ventaActualizada);
        }
        return null;
    }

    /**
     * Elimina una venta y todos sus detalles asociados.
     */
    public boolean eliminarVenta(Integer idVenta) {
        if (ventaRepository.existsById(idVenta)) {
            // Eliminar primero los detalles por la relación de clave foránea
            detalleVentaRepository.deleteByVenta_IdVenta(idVenta);
            // Luego eliminar la venta
            ventaRepository.deleteById(idVenta);
            return true;
        }
        return false;
    }

    /**
     * Convierte una entidad Venta a VentaDTO.
     */
    private VentaDTO convertirAVentaDTO(Venta venta) {
        VentaDTO dto = new VentaDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setIdCliente(venta.getIdCliente());
        dto.setIdEmpleado(venta.getIdEmpleado());
        dto.setFechaVenta(venta.getFechaVenta());
        dto.setMetodoPago(venta.getMetodoPago() != null ? venta.getMetodoPago().name() : null);
        dto.setTotal(venta.getTotal());
        dto.setIdMetodoPago(venta.getIdMetodoPago());
        return dto;
    }

    /**
     * Convierte un VentaDTO a entidad Venta.
     */
    private Venta convertirAVentaEntity(VentaDTO ventaDTO) {
        Venta venta = new Venta();

        if (ventaDTO.getIdVenta() != null) {
            venta.setIdVenta(ventaDTO.getIdVenta());
        }
        venta.setIdCliente(ventaDTO.getIdCliente() != null ? ventaDTO.getIdCliente() : 0);
        venta.setIdEmpleado(ventaDTO.getIdEmpleado() != null ? ventaDTO.getIdEmpleado() : 0);
        venta.setFechaVenta(ventaDTO.getFechaVenta());

        if (ventaDTO.getMetodoPago() != null && !ventaDTO.getMetodoPago().trim().isEmpty()) {
            try {
                venta.setMetodoPago(MetodoPago.valueOf(ventaDTO.getMetodoPago().trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Método de pago inválido: " + ventaDTO.getMetodoPago());
            }
        } else {
            throw new IllegalArgumentException("El método de pago es obligatorio");
        }

        venta.setTotal(ventaDTO.getTotal() != null ? ventaDTO.getTotal() : BigDecimal.ZERO);
        venta.setIdMetodoPago(ventaDTO.getIdMetodoPago() != null ? ventaDTO.getIdMetodoPago() : 0);

        return venta;
    }

    /**
     * Convierte una entidad DetalleVenta a DetalleVentaDTO.
     */
    private DetalleVentaDTO convertirADetalleVentaDTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setIdDetalle(detalle.getIdDetalle());
        dto.setIdVenta(detalle.getVenta().getIdVenta());
        dto.setIdProducto(detalle.getIdProducto());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    /**
     * Convierte un DetalleVentaDTO a entidad DetalleVenta.
     */
    /**
     * Convierte un DetalleVentaDTO a entidad DetalleVenta.
     */
    private DetalleVenta convertirADetalleVentaEntity(DetalleVentaDTO detalleDTO) {
        DetalleVenta detalle = new DetalleVenta();

        if (detalleDTO.getIdDetalle() != null) {
            detalle.setIdDetalle(detalleDTO.getIdDetalle());
        }

        detalle.setIdVenta(detalleDTO.getIdVenta());

        detalle.setIdProducto(detalleDTO.getIdProducto());
        detalle.setCantidad(detalleDTO.getCantidad());
        detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());

        return detalle;
    }

    /**
     * Actualiza los campos de una entidad Venta con los datos del DTO.
     */
    private void actualizarCamposVenta(Venta venta, VentaDTO ventaDTO) {
        if (ventaDTO.getIdCliente() != null) {
            venta.setIdCliente(ventaDTO.getIdCliente());
        }
        if (ventaDTO.getIdEmpleado() != null) {
            venta.setIdEmpleado(ventaDTO.getIdEmpleado());
        }
        if (ventaDTO.getFechaVenta() != null) {
            venta.setFechaVenta(ventaDTO.getFechaVenta());
        }
        if (ventaDTO.getMetodoPago() != null && !ventaDTO.getMetodoPago().trim().isEmpty()) {
            try {
                venta.setMetodoPago(MetodoPago.valueOf(ventaDTO.getMetodoPago().trim()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Método de pago inválido: " + ventaDTO.getMetodoPago());
            }
        }
        if (ventaDTO.getTotal() != null) {
            venta.setTotal(ventaDTO.getTotal());
        }
        if (ventaDTO.getIdMetodoPago() != null) {
            venta.setIdMetodoPago(ventaDTO.getIdMetodoPago());
        }
    }
}