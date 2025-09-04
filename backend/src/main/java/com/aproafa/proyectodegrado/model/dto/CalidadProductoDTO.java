package com.aproafa.proyectodegrado.model.dto;

import com.aproafa.proyectodegrado.model.entity.CalidadEnum;
import lombok.Data;

/**
 * DTO para la entidad CalidadProducto. Contiene la evaluación de calidad de una producción.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
public class CalidadProductoDTO {

  /** Identificador único de la evaluación de calidad. */
  private Integer idCalidad;

  /** Identificador de la producción evaluada. */
  private Integer idProduccion;

  /** Nivel de calidad asignado. */
  private CalidadEnum calidad;

  /** Observaciones adicionales sobre la calidad. */
  private String observaciones;
}
