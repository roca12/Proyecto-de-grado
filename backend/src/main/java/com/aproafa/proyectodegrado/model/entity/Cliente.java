package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

/**
 * Entidad que representa un cliente asociado a una finca dentro del sistema.
 *
 * <p>Contiene la relación con la entidad {@link Persona} y {@link Finca}, además de información
 * sobre el tipo de cliente y la fecha de registro.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "cliente")
public class Cliente {

  /** Identificador único del cliente. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_cliente")
  private Integer idCliente;

  /** Persona asociada al cliente (relación muchos a uno). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_persona", nullable = false)
  private Persona persona;

  /** Finca a la cual está asociado el cliente (relación muchos a uno). */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_finca", nullable = false)
  private Finca finca;

  /** Tipo de cliente (por ejemplo, "mayorista", "minorista", etc.). */
  @Column(name = "tipo_cliente", nullable = false)
  private String tipoCliente;

  /** Fecha en la que el cliente fue registrado en el sistema. */
  @Column(name = "fecha_registro", nullable = false)
  private LocalDate fechaRegistro;
}
