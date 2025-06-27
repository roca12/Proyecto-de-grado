package com.example.proyecto_de_grado.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * DTO para la entidad InventarioProducto. Representa el stock y fecha de actualización de un
 * producto en inventario.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class InventarioProductoDTO {

  /** Identificador único del registro de inventario. */
  private Integer idInventario;

  /** Identificador del producto asociado. */
  private Integer idProducto;

  /** Cantidad disponible en inventario. */
  private BigDecimal cantidad;

  /** Fecha y hora de la última actualización. */
  private LocalDateTime fechaActualizacion;
}
