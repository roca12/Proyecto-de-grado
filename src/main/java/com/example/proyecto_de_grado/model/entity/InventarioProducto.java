package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * Entidad que representa el inventario asociado a un producto específico.
 *
 * <p>Esta clase permite llevar el control de la cantidad disponible de cada producto y la fecha de
 * la última actualización del inventario.
 *
 * <p>Relaciones:
 *
 * <ul>
 *   <li>Uno a Uno con {@link Producto}: Cada inventario está vinculado a un único producto.
 * </ul>
 *
 * <p>Atributos:
 *
 * <ul>
 *   <li>{@code idInventario}: Identificador único del registro de inventario.
 *   <li>{@code producto}: Producto al que pertenece este inventario.
 *   <li>{@code cantidad}: Cantidad actual disponible del producto en inventario.
 *   <li>{@code fechaActualizacion}: Fecha y hora de la última actualización del inventario.
 * </ul>
 */
@Data
@Entity
@Table(name = "inventario_producto")
public class InventarioProducto {

  /** Identificador único del inventario. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_inventario")
  private Integer idInventario;

  /** Producto al que pertenece este inventario. */
  @OneToOne
  @JoinColumn(name = "id_producto", nullable = false)
  private Producto producto;

  /** Cantidad actual del producto en inventario. */
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  /** Fecha y hora de la última actualización del inventario. */
  @Column(name = "fecha_actualizacion")
  private LocalDateTime fechaActualizacion;
}
