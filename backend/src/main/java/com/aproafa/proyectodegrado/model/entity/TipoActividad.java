package com.aproafa.proyectodegrado.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entidad que representa un tipo de actividad dentro del sistema.
 *
 * <p>Contiene información sobre el nombre del tipo de actividad. Es una entidad importante para
 * categorizar las actividades dentro de la finca.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Entity
@Table(name = "Tipo_Actividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoActividad {

  /** Identificador único del tipo de actividad. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer idTipoActividad;

  /** Nombre del tipo de actividad. No puede estar vacío y debe ser único. */
  @NotBlank
  @Column(nullable = false, unique = true, length = 100)
  private String nombre;
}
