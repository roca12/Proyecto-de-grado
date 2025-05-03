package com.example.proyecto_de_grado.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa a un proveedor dentro del sistema.
 *
 * <p>Incluye relaciones con {@link Persona} y {@link Finca}, además de información como el nombre
 * comercial y los datos de contacto.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "proveedor")
public class Proveedor {

  /** Identificador único del proveedor. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_proveedor")
  private Integer idProveedor;

  /** Persona asociada al proveedor (relación muchos a uno). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_persona", nullable = false)
  @JsonIgnore
  private Persona persona;
  /** Finca asociada al proveedor (relación muchos a uno). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_finca")
  @JsonIgnore
  private Finca finca;
  /** Nombre del proveedor (razón social o nombre comercial). */
  @Column(name = "nombre", length = 100, nullable = false)
  private String nombre;

  /** Información de contacto del proveedor (nombre del encargado, teléfono alterno, etc.). */
  @Column(name = "contacto", length = 100)
  private String contacto;
}
