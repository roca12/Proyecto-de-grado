package com.aproafa.proyectodegrado.service;

import com.aproafa.proyectodegrado.model.dto.DetalleVentaDTO;
import com.aproafa.proyectodegrado.model.dto.VentaDTO;
import com.aproafa.proyectodegrado.model.entity.*;
import com.aproafa.proyectodegrado.repository.DetalleVentaRepository;
import com.aproafa.proyectodegrado.repository.ProduccionRepository;
import com.aproafa.proyectodegrado.repository.VentaRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VentaService {

  private final VentaRepository ventaRepository;
  private final DetalleVentaRepository detalleVentaRepository;

  @Autowired
  public VentaService(
      VentaRepository ventaRepository, DetalleVentaRepository detalleVentaRepository) {
    this.ventaRepository = ventaRepository;
    this.detalleVentaRepository = detalleVentaRepository;
  }

  @Transactional(readOnly = true)
  public VentaDTO obtenerVentaPorId(Integer idVenta) {
    Optional<Venta> ventaOptional = ventaRepository.findById(idVenta);
    return ventaOptional.map(this::convertirAVentaDTO).orElse(null);
  }

  @Transactional(readOnly = true)
  public List<DetalleVentaDTO> obtenerDetallesDeVenta(Integer idVenta) {
    List<DetalleVenta> detalles = detalleVentaRepository.findByVenta_IdVenta(idVenta);
    return detalles.stream().map(this::convertirADetalleVentaDTO).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<VentaDTO> obtenerTodasLasVentas() {
    List<Venta> ventas = ventaRepository.findAll();
    return ventas.stream().map(this::convertirAVentaDTO).collect(Collectors.toList());
  }

  public VentaDTO guardarVenta(VentaDTO ventaDTO) {
    if (ventaDTO.getMetodoPago() == null || ventaDTO.getMetodoPago().trim().isEmpty()) {
      throw new IllegalArgumentException("El método de pago es obligatorio");
    }

    Venta venta = convertirAVentaEntity(ventaDTO);
    if (venta.getFechaVenta() == null) {
      venta.setFechaVenta(LocalDateTime.now());
    }

    if (venta.getTotal() == null) {
      venta.setTotal(BigDecimal.ZERO);
    }

    Venta ventaGuardada = ventaRepository.save(venta);
    return convertirAVentaDTO(ventaGuardada);
  }

  @Autowired private ProduccionRepository produccionRepository;

  public VentaDTO guardarVentaConDetalles(VentaDTO ventaDTO, List<DetalleVentaDTO> detallesDTO) {
    if (ventaDTO.getMetodoPago() == null || ventaDTO.getMetodoPago().trim().isEmpty()) {
      throw new IllegalArgumentException("El método de pago es obligatorio");
    }

    if (detallesDTO == null || detallesDTO.isEmpty()) {
      throw new IllegalArgumentException("Una venta debe tener al menos un detalle");
    }

    BigDecimal totalCalculado = BigDecimal.ZERO;
    for (DetalleVentaDTO detalleDTO : detallesDTO) {
      if (detalleDTO.getIdProduccion() == null
          || detalleDTO.getCantidad() == null
          || detalleDTO.getPrecioUnitario() == null) {
        throw new IllegalArgumentException("Todos los campos del detalle son obligatorios");
      }

      BigDecimal precioUnitario = convertToBigDecimal(detalleDTO.getPrecioUnitario());
      BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(detalleDTO.getCantidad()));
      totalCalculado = totalCalculado.add(subtotal);
    }

    ventaDTO.setTotal(totalCalculado);
    VentaDTO ventaGuardada = guardarVenta(ventaDTO);

    if (ventaGuardada == null || ventaGuardada.getIdVenta() == null) {
      throw new RuntimeException("Error al guardar la venta");
    }

    for (DetalleVentaDTO detalleDTO : detallesDTO) {
      detalleDTO.setIdVenta(ventaGuardada.getIdVenta());
      DetalleVenta detalleEntity = convertirADetalleVentaEntity(detalleDTO);
      detalleVentaRepository.save(detalleEntity);
    }

    return ventaGuardada;
  }

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

  public boolean eliminarVenta(Integer idVenta) {
    if (ventaRepository.existsById(idVenta)) {
      detalleVentaRepository.deleteByVenta_IdVenta(idVenta);
      ventaRepository.deleteById(idVenta);
      return true;
    }
    return false;
  }

  private VentaDTO convertirAVentaDTO(Venta venta) {
    VentaDTO dto = new VentaDTO();
    dto.setIdVenta(venta.getIdVenta());
    dto.setIdCliente(venta.getIdCliente());
    dto.setIdPersona(venta.getIdPersona());
    dto.setFechaVenta(venta.getFechaVenta());
    dto.setMetodoPago(venta.getMetodoPago() != null ? venta.getMetodoPago().name() : null);
    dto.setTotal(venta.getTotal());
    dto.setIdFinca(venta.getFinca() != null ? venta.getFinca().getId() : null);
    return dto;
  }

  private Venta convertirAVentaEntity(VentaDTO ventaDTO) {
    Venta venta = new Venta();

    if (ventaDTO.getIdVenta() != null) {
      venta.setIdVenta(ventaDTO.getIdVenta());
    }
    venta.setIdCliente(ventaDTO.getIdCliente() != null ? ventaDTO.getIdCliente() : 0);
    venta.setIdPersona(ventaDTO.getIdPersona() != null ? ventaDTO.getIdPersona() : 0);
    venta.setFechaVenta(
        ventaDTO.getFechaVenta() != null ? ventaDTO.getFechaVenta() : LocalDateTime.now());

    if (ventaDTO.getIdFinca() != null) {
      Finca finca = new Finca(ventaDTO.getIdFinca());
      venta.setFinca(finca);
    }
    if (ventaDTO.getMetodoPago() != null && !ventaDTO.getMetodoPago().trim().isEmpty()) {
      try {
        venta.setMetodoPago(MetodoPago.fromString(ventaDTO.getMetodoPago().trim()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(
            "Método de pago inválido: "
                + ventaDTO.getMetodoPago()
                + ". Valores aceptados: "
                + Arrays.toString(MetodoPago.values()));
      }
    }

    venta.setTotal(convertToBigDecimal(ventaDTO.getTotal()));
    return venta;
  }

  @Transactional(readOnly = true)
  public List<VentaDTO> obtenerVentasPorFinca(Integer idFinca) {
    return ventaRepository.findByFinca_Id(idFinca).stream()
        .map(this::convertirAVentaDTO)
        .collect(Collectors.toList());
  }

  private DetalleVentaDTO convertirADetalleVentaDTO(DetalleVenta detalle) {
    DetalleVentaDTO dto = new DetalleVentaDTO();
    dto.setIdDetalle(detalle.getIdDetalle());
    dto.setIdVenta(detalle.getVenta().getIdVenta());
    dto.setIdProduccion(detalle.getIdProduccion());
    dto.setCantidad(detalle.getCantidad());
    dto.setPrecioUnitario(detalle.getPrecioUnitario());
    dto.setSubtotal(detalle.getSubtotal());
    return dto;
  }

  private DetalleVenta convertirADetalleVentaEntity(DetalleVentaDTO detalleDTO) {
    DetalleVenta detalle = new DetalleVenta();

    if (detalleDTO.getIdDetalle() != null) {
      detalle.setIdDetalle(detalleDTO.getIdDetalle());
    }

    detalle.setIdVenta(detalleDTO.getIdVenta());
    detalle.setIdProduccion(detalleDTO.getIdProduccion());
    detalle.setCantidad(detalleDTO.getCantidad());
    detalle.setPrecioUnitario(convertToBigDecimal(detalleDTO.getPrecioUnitario()));

    return detalle;
  }

  private void actualizarCamposVenta(Venta venta, VentaDTO ventaDTO) {
    if (ventaDTO.getIdCliente() != null) {
      venta.setIdCliente(ventaDTO.getIdCliente());
    }
    if (ventaDTO.getIdPersona() != null) {
      venta.setIdPersona(ventaDTO.getIdPersona());
    }
    if (ventaDTO.getFechaVenta() != null) {
      venta.setFechaVenta(ventaDTO.getFechaVenta());
    }
    if (ventaDTO.getMetodoPago() != null && !ventaDTO.getMetodoPago().trim().isEmpty()) {
      try {
        venta.setMetodoPago(MetodoPago.valueOf(ventaDTO.getMetodoPago().trim().toUpperCase()));
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Método de pago inválido: " + ventaDTO.getMetodoPago());
      }
    }
    if (ventaDTO.getTotal() != null) {
      venta.setTotal(convertToBigDecimal(ventaDTO.getTotal()));
    }
  }

  private BigDecimal convertToBigDecimal(Object value) {
    if (value == null) {
      return BigDecimal.ZERO;
    }
    if (value instanceof BigDecimal) {
      return (BigDecimal) value;
    }
    if (value instanceof String) {
      return new BigDecimal((String) value);
    }
    if (value instanceof Number) {
      return BigDecimal.valueOf(((Number) value).doubleValue());
    }
    throw new IllegalArgumentException("No se puede convertir a BigDecimal: " + value.getClass());
  }
}
