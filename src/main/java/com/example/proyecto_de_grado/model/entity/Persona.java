package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad base que representa a una persona dentro del sistema.
 *
 * <p>Contiene información común a todas las personas, como nombre, apellido, tipo de
 * identificación, número de identificación, correo electrónico, teléfono y dirección.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persona")
public class Persona {

  /** Identificador único de la persona. */
  @Id
  @Column(name = "id_persona")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int idPersona;

  /** Nombre de la persona. */
  private String nombre;

  /** Apellido de la persona. */
  private String apellido;

  /** Tipo de identificación de la persona (por ejemplo, cédula, NIT, etc.). */
  @Column(name = "tipo_id")
  private int tipoId;

  /** Número de identificación de la persona. */
  @Column(name = "numero_identificacion")
  private String numeroIdentificacion;

  /** Correo electrónico de la persona. */
  private String email;

  /** Número de teléfono de la persona. */
  private String telefono;

  /** Dirección de la persona. */
  private String direccion;
}
