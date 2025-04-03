package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
@Table(name = "insumo")
public class Insumo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_insumo")
  private int idInsumo;

  @Column(name = "nombre", length = 100, nullable = false)
  private String nombre;

  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(name = "unidad_medida", nullable = false)
  private UnidadMedida unidadMedida;

  @ManyToOne
  @JoinColumn(name = "id_proveedor", nullable = false)
  private Proveedor proveedor;

  @Column(name = "cantidad_disponible", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidadDisponible;
}
