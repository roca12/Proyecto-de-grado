package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

/**
 * Entidad que representa un ciclo de producción agrícola de un producto en una finca específica.
 *
 * <p>Contiene información relevante sobre fechas de siembra y cosecha, estado de la producción,
 * cantidad cosechada y evaluación de calidad.
 *
 * <p>Relaciones:
 *
 * <ul>
 *   <li>Muchos a Uno con {@link Producto}: Un producto puede estar presente en múltiples
 *       producciones.
 *   <li>Muchos a Uno con {@link Finca}: Cada producción pertenece a una finca específica.
 *   <li>Uno a Muchos con {@link CalidadProducto}: Una producción puede tener múltiples evaluaciones
 *       de calidad.
 * </ul>
 *
 * <p>Atributos:
 *
 * <ul>
 *   <li>{@code idProduccion}: Identificador único de la producción.
 *   <li>{@code producto}: Producto agrícola que se está cultivando.
 *   <li>{@code finca}: Finca en la que se lleva a cabo la producción.
 *   <li>{@code fechaSiembra}: Fecha en la que se sembró el producto.
 *   <li>{@code fechaCosecha}: Fecha en la que se cosechó el producto (puede ser nula si aún no se
 *       ha cosechado).
 *   <li>{@code estado}: Estado actual de la producción (enum: EN_CRECIMIENTO, LISTO_PARA_COSECHA,
 *       COSECHADO).
 *   <li>{@code cantidadCosechada}: Cantidad total cosechada, expresada como número decimal.
 *   <li>{@code calidades}: Lista de evaluaciones de calidad asociadas a esta producción.
 * </ul>
 */
@Data
@Entity
@Table(name = "produccion")
public class Produccion {

  /** Identificador único de la producción. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_produccion")
  private Integer idProduccion;

  /** Producto agrícola cultivado en esta producción. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_producto", nullable = false)
  private Producto producto;

  /** Finca donde se realiza la producción. */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_finca", nullable = false)
  private Finca finca;

  /** Fecha de siembra del producto. */
  @Column(name = "fecha_siembra", nullable = false)
  private LocalDate fechaSiembra;

  /** Fecha de cosecha del producto (opcional). */
  @Column(name = "fecha_cosecha")
  private LocalDate fechaCosecha;

  /** Estado actual de la producción (EN_CRECIMIENTO, LISTO_PARA_COSECHA, COSECHADO). */
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoProduccion estado;

  /** Cantidad total cosechada, expresada en una unidad determinada (ej. kilos). */
  @Column(name = "cantidad_cosechada", precision = 10, scale = 2)
  private BigDecimal cantidadCosechada;

  /** Evaluaciones de calidad asociadas a esta producción. */
  @OneToMany(mappedBy = "produccion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<CalidadProducto> calidades;

  /** Constructor sin-args que JPA requiere para instanciación. */
  public Produccion() {}

  /**
   * Constructor de conveniencia para referenciar una producción por su ID.
   *
   * @param idProduccion Identificador de la producción.
   */
  public Produccion(Integer idProduccion) {
    this.idProduccion = idProduccion;
  }
}
