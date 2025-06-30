package com.example.proyecto_de_grado.model.dto;

import java.math.BigDecimal;
import lombok.Data;

/**
 * DTO (Data Transfer Object) para la gestión de detalles de venta.
 *
 * <p>Esta clase representa el detalle de cada producto vendido en una transacción, incluyendo
 * información específica como cantidad, precio unitario y subtotal de cada ítem. Facilita la
 * transferencia de datos entre capas de la aplicación para el manejo de líneas de venta.
 *
 * <p>Se basa en la estructura de la tabla 'detalle_venta' y mantiene la relación con las ventas a
 * través del identificador de venta correspondiente.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class DetalleVentaDTO {

  /** Identificador único del detalle de venta en el sistema. */
  private Integer idDetalle;

  /** Identificador de la venta a la que pertenece este detalle. */
  private Integer idVenta;

  /** Identificador del producto vendido. */
  private Integer idProducto;

  /** Cantidad de unidades vendidas del producto. */
  private Integer cantidad;

  /** Precio unitario del producto en el momento de la venta con precisión decimal (10,2). */
  private BigDecimal precioUnitario;

  /** Subtotal de la línea de venta (cantidad × precio unitario) con precisión decimal (10,2). */
  private BigDecimal subtotal;
}
