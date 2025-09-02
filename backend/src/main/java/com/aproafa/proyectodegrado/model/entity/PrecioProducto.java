package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * Entidad que representa el historial de precios de un producto en un rango de fechas determinado.
 *
 * <p>Permite llevar un control de cómo han variado los precios de un producto a lo largo del
 * tiempo, definiendo un precio con fecha de inicio obligatoria y fecha de fin opcional.
 *
 * <p>Relaciones:
 *
 * <ul>
 *   <li>Muchos a Uno con {@link Producto}: Un producto puede tener múltiples precios en distintos
 *       periodos.
 * </ul>
 *
 * <p>Atributos:
 *
 * <ul>
 *   <li>{@code idPrecio}: Identificador único del precio.
 *   <li>{@code producto}: Producto al que pertenece este precio.
 *   <li>{@code fechaInicio}: Fecha desde la cual entra en vigencia el precio.
 *   <li>{@code fechaFin}: Fecha hasta la cual el precio es válido (puede ser {@code null} si está
 *       vigente).
 *   <li>{@code precio}: Valor del precio del producto en el periodo definido.
 * </ul>
 */
@Data
@Entity
@Table(name = "precio_producto")
public class PrecioProducto {

  /** Identificador único del precio registrado. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_precio")
  private Integer idPrecio;

  /** Producto al que se asocia este precio. */
  @ManyToOne
  @JoinColumn(name = "id_producto", nullable = false)
  private Producto producto;

  /** Fecha de inicio de vigencia del precio. */
  @Column(name = "fecha_inicio", nullable = false)
  private LocalDate fechaInicio;

  /** Fecha de fin de vigencia del precio (puede ser nula si el precio aún está vigente). */
  @Column(name = "fecha_fin")
  private LocalDate fechaFin;

  /** Valor del precio asignado al producto. */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal precio;
}
