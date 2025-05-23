package com.example.proyecto_de_grado.model.dto;

import com.example.proyecto_de_grado.model.entity.EstadoProduccion;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * DTO para la entidad Produccion. Contiene datos de siembra, cosecha y estado de producción de un
 * producto.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class ProduccionDTO {

  /** Identificador único de la producción. */
  private Integer idProduccion;

  /** Identificador del producto producido. */
  private Integer idProducto;

  /** Identificador de la finca donde se produce. */
  private Integer idFinca;

  /** Fecha de siembra. */
  private LocalDate fechaSiembra;

  /** Fecha de cosecha (puede ser null si no se ha cosechado). */
  private LocalDate fechaCosecha;

  /** Estado actual de la producción. */
  private EstadoProduccion estado;

  /** Cantidad cosechada (solo si el estado es COSECHADO). */
  private BigDecimal cantidadCosechada;
}
