package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa la evaluación de calidad asociada a una producción específica.
 *
 * <p>Esta entidad permite registrar la clasificación de calidad (mediante un enumerado) y
 * observaciones detalladas sobre los productos obtenidos en una producción agrícola.
 *
 * <p>Relaciones:
 *
 * <ul>
 *   <li>Muchos a Uno con {@link Produccion}: Cada evaluación de calidad está asociada a una sola
 *       producción.
 * </ul>
 *
 * <p>Atributos:
 *
 * <ul>
 *   <li>{@code idCalidad}: Identificador único de la evaluación de calidad.
 *   <li>{@code produccion}: Producción a la que pertenece esta evaluación.
 *   <li>{@code calidad}: Nivel de calidad asignado (EXCELENTE, BUENA, REGULAR, MALA).
 *   <li>{@code observaciones}: Comentarios adicionales u observaciones relevantes al evaluar la
 *       calidad.
 * </ul>
 */
@Data
@Entity
@Table(name = "calidad_producto")
public class CalidadProducto {

  /** Identificador único de la evaluación de calidad. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_calidad")
  private Integer idCalidad;

  /** Producción a la que se evalúa su calidad. */
  @ManyToOne
  @JoinColumn(name = "id_produccion", nullable = false)
  private Produccion produccion;

  /** Clasificación de calidad del producto evaluado. */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CalidadEnum calidad;

  /** Comentarios u observaciones adicionales sobre la evaluación de calidad. */
  @Column(columnDefinition = "TEXT")
  private String observaciones;
}
