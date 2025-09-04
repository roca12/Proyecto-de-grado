package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "venta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_venta")
  private Integer idVenta;

  @Column(name = "id_cliente", nullable = false)
  private Integer idCliente;

  @Column(name = "id_persona", nullable = false)
  private Integer idPersona;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_finca", nullable = false)
  private Finca finca;

  @Column(name = "fecha_venta", nullable = false)
  private LocalDateTime fechaVenta;

  @Enumerated(EnumType.STRING)
  @Column(name = "metodo_pago", nullable = false)
  private MetodoPago metodoPago;

  @Column(name = "total", precision = 10, scale = 2, nullable = false)
  private BigDecimal total = BigDecimal.ZERO;

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<DetalleVenta> detalles = new ArrayList<>();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_cliente", insertable = false, updatable = false)
  private Cliente cliente;

  public Venta(
      Integer idCliente,
      Integer idPersona,
      LocalDateTime fechaVenta,
      MetodoPago metodoPago,
      BigDecimal total) {
    this.idCliente = idCliente;
    this.idPersona = idPersona;
    this.fechaVenta = fechaVenta;
    this.metodoPago = metodoPago;
    this.total = total != null ? total : BigDecimal.ZERO;
  }

  public void agregarDetalle(DetalleVenta detalle) {
    detalles.add(detalle);
    detalle.setVenta(this);
  }

  public void calcularTotalDesdeDetalles() {
    this.total =
        detalles.stream()
            .map(DetalleVenta::getSubtotal)
            .filter(subtotal -> subtotal != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
