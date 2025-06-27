package com.example.proyecto_de_grado.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO para la entidad PrecioProducto. Registra el precio histórico de un producto en un rango de
 * fechas.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class PrecioProductoDTO {

  /** Identificador único del registro de precio. */
  private Integer idPrecio;

  /** Identificador del producto asociado. */
  private Integer idProducto;

  /** Fecha de inicio de vigencia del precio. */
  private LocalDate fechaInicio;

  /** Fecha de fin de vigencia del precio (null si aún vigente). */
  private LocalDate fechaFin;

  /** Valor del precio. */
  private BigDecimal precio;
}
