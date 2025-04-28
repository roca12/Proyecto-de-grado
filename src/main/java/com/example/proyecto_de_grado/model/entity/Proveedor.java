package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa a un proveedor dentro del sistema.
 *
 * <p>Contiene información básica sobre el proveedor, como su nombre y datos de contacto.
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
  private int idProveedor;

  /** Nombre del proveedor. */
  @Column(name = "nombre", length = 100, nullable = false)
  private String nombre;

  /** Información de contacto del proveedor. */
  @Column(name = "contacto", length = 100)
  private String contacto;
}
