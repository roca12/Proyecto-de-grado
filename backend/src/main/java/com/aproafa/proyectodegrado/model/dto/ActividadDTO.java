package com.aproafa.proyectodegrado.model.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para representar actividades agrícolas.
 *
 * <p>Esta clase se utiliza para transferir datos de actividades entre diferentes capas de la
 * aplicación, especialmente en las operaciones de la API REST. Contiene información básica sobre
 * actividades programadas en las fincas del sistema.
 *
 * @author Anderson Zuluaga
 * @version 1.0
 * @since 2023
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActividadDTO {
  /** Identificador único de la actividad. */
  private Integer idActividad;

  /** Identificador de la finca donde se realiza la actividad. */
  private Integer idFinca;

  /** Identificador del tipo de actividad (siembra, cosecha, fumigación, etc.). */
  private Integer idTipoActividad;

  /** Fecha de inicio programada para la actividad. */
  private LocalDate fechaInicio;

  /** Fecha de finalización estimada de la actividad. */
  private LocalDate fechaFin;

  /** Descripción detallada de la actividad a realizar. */
  private String descripcion;

  private List<UsoInsumoDTO> usosInsumos;
}
