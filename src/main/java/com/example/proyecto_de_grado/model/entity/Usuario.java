package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa a un usuario dentro del sistema, extendiendo la entidad {@link Persona}.
 *
 * <p>El usuario tiene información adicional como su contraseña, tipo de usuario (ADMIN o USER), y
 * la finca a la que está asociado.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "usuario")
@PrimaryKeyJoinColumn(name = "id_persona")
public class Usuario extends Persona {

  /** Contraseña del usuario. */
  @Column(name = "contraseña")
  private String contraseña;

  /** Tipo de usuario: "ADMIN" o "USER". */
  @Column(name = "tipo_usuario")
  private String tipoUsuario;

  /** Finca a la que está asociado el usuario. */
  @ManyToOne
  @JoinColumn(name = "id_finca")
  private Finca finca;
}
