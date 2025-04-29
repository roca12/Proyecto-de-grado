package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * Entidad que representa la compra de un insumo realizada a un proveedor.
 *
 * <p>Contiene información sobre el insumo comprado, la cantidad adquirida, el precio unitario, la
 * fecha de compra y el proveedor asociado.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "compra_insumo")
public class CompraInsumo {

  /** Identificador único de la compra de insumo. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_compra")
  private int idCompra;

  /** Insumo adquirido en la compra (relación muchos a uno con {@link Insumo}). */
  @ManyToOne
  @JoinColumn(name = "id_insumo", nullable = false)
  @NotNull(message = "El insumo es obligatorio.")
  private Insumo insumo;

  /** Cantidad del insumo adquirida en la compra. Debe ser mayor que cero. */
  @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
  @Positive(message = "La cantidad debe ser mayor que cero.")
  private BigDecimal cantidad;

  /** Precio unitario del insumo al momento de la compra. Debe ser mayor que cero. */
  @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
  @Positive(message = "El precio unitario debe ser mayor que cero.")
  private BigDecimal precioUnitario;

  /** Fecha en la que se realizó la compra. No puede ser una fecha futura. */
  @Column(name = "fecha_compra", nullable = false)
  @PastOrPresent(message = "La fecha de compra no puede ser futura.")
  private LocalDate fechaCompra;

  /** Proveedor que suministró el insumo (relación muchos a uno con {@link Proveedor}). */
  @ManyToOne
  @JoinColumn(name = "id_proveedor", nullable = false)
  @NotNull(message = "El proveedor es obligatorio.")
  private Proveedor proveedor;
}
