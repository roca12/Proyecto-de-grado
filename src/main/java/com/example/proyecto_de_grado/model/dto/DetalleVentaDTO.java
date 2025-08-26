package com.example.proyecto_de_grado.model.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para la gestión de detalles de venta.
 *
 * <p>Esta clase representa el detalle de cada producción vendida en una transacción, incluyendo
 * cantidad, precio unitario y subtotal de cada ítem.
 */
@Data
public class DetalleVentaDTO {

  /** Identificador único del detalle de venta en el sistema. */
  private Integer idDetalle;

  /** Identificador de la venta a la que pertenece este detalle. */
  private Integer idVenta;

  /** Identificador de la producción vendida. */
  private Integer idProduccion;

  /** Cantidad de unidades vendidas. */
  private Integer cantidad;

  /** Precio unitario con precisión decimal. */
  private BigDecimal precioUnitario;

  /** Subtotal de la línea de venta. */
  private BigDecimal subtotal;
}
