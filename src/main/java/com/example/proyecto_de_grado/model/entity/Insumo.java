package com.example.proyecto_de_grado.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Data;

/**
 * Entidad que representa un insumo dentro del sistema.
 *
 * <p>Contiene información sobre el nombre, descripción, unidad de medida, proveedor y cantidad
 * disponible del insumo.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "insumo")
public class Insumo {

  /** Identificador único del insumo. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_insumo")
  private int idInsumo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_finca", nullable = false)
  @JsonIgnore
  private Finca finca;


  /** Nombre del insumo. */
  @Column(name = "nombre", length = 100, nullable = false)
  private String nombre;

  /** Descripción detallada del insumo. */
  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  /** Unidad de medida para el insumo, representada por un valor enumerado. */
  @Enumerated(EnumType.STRING)
  @Column(name = "unidad_medida", nullable = false)
  private UnidadMedida unidadMedida;

  /** Proveedor que suministra el insumo (relación muchos a uno con {@link Proveedor}). */
  @ManyToOne
  @JoinColumn(name = "id_proveedor", nullable = false)
  private Proveedor proveedor;

  /** Cantidad disponible del insumo en inventario. */
  @Column(name = "cantidad_disponible", nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidadDisponible;
}
