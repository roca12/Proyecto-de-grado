package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entidad que representa el detalle de una venta, basado en producción. */
@Entity
@Table(name = "detalle_venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_detalle")
  private Integer idDetalle;

  @Column(name = "id_venta", nullable = false)
  private Integer idVenta;

  @Column(name = "id_produccion", nullable = false)
  private Integer idProduccion;

  @Column(name = "cantidad", nullable = false)
  private Integer cantidad;

  @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
  private BigDecimal precioUnitario;

  @Column(
      name = "subtotal",
      precision = 10,
      scale = 2,
      nullable = false,
      insertable = false,
      updatable = false)
  private BigDecimal subtotal;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_venta", insertable = false, updatable = false)
  private Venta venta;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_produccion", insertable = false, updatable = false)
  private Produccion produccion;

  public DetalleVenta(
      Integer idVenta, Integer idProduccion, Integer cantidad, BigDecimal precioUnitario) {
    this.idVenta = idVenta;
    this.idProduccion = idProduccion;
    this.cantidad = cantidad;
    this.precioUnitario = precioUnitario;
  }

  public BigDecimal calcularSubtotal() {
    if (this.cantidad != null && this.precioUnitario != null) {
      return this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
    }
    return BigDecimal.ZERO;
  }
}
