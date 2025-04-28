package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

/**
 * Entidad que representa una actividad realizada dentro de una finca.
 *
 * <p>Contiene información relacionada con el tipo de actividad, las fechas de inicio y fin, y una
 * descripción opcional.
 *
 * <p>Se relaciona con la entidad {@link TipoActividad} y con una finca mediante su identificador.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Entity
@Table(name = "Actividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Actividad {

  /** Identificador único de la actividad. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idActividad;

  /** Identificador de la finca asociada a la actividad. */
  @NotNull
  @Column(name = "id_finca", nullable = false)
  private Integer idFinca;

  /** Tipo de actividad realizada (relación con la entidad TipoActividad). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_tipo_actividad", nullable = false)
  private TipoActividad tipoActividad;

  /** Fecha de inicio de la actividad. Debe ser hoy o una fecha futura. */
  @NotNull
  @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
  private LocalDate fechaInicio;

  /** Fecha de finalización de la actividad (opcional). */
  private LocalDate fechaFin;

  /** Descripción adicional sobre la actividad (opcional). */
  private String descripcion;
}
