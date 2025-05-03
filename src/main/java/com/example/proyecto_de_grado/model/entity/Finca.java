package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Entidad que representa una finca dentro del sistema.
 *
 * <p>Contiene información básica como el nombre, la ubicación y el encargado de la finca. Además,
 * establece una relación con los usuarios que pertenecen a esta finca.</p>
 *
 * <p>Relaciones:
 * <ul>
 *   <li>Uno a Muchos con {@link Usuario}: Una finca puede tener varios usuarios asociados.</li>
 * </ul></p>
 *
 * <p>Atributos:
 * <ul>
 *   <li>{@code id}: Identificador único de la finca.</li>
 *   <li>{@code nombre}: Nombre de la finca.</li>
 *   <li>{@code ubicacion}: Ubicación geográfica de la finca.</li>
 *   <li>{@code encargado}: Identificador del usuario encargado de la finca.</li>
 *   <li>{@code usuarios}: Lista de usuarios asociados a la finca.</li>
 * </ul></p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Data
@Entity
@Table(name = "finca")
public class Finca {

  /** Identificador único de la finca. */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_finca")
  private Integer id;

  /** Nombre de la finca. */
  @Column(nullable = false)
  private String nombre;

  /** Ubicación geográfica de la finca. */
  private String ubicacion;

  /** Identificador del usuario encargado de la finca. */
  private String encargado;

  /** Lista de usuarios asociados a la finca. */
  @OneToMany(mappedBy = "finca", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Usuario> usuarios;

  /**
   * Constructor sin-args que JPA requiere para instanciación.
   */
  public Finca() { }

  /**
   * Constructor de conveniencia para referenciar una finca por su ID.
   *
   * @param id Identificador de la finca.
   */
  public Finca(Integer id) {
    this.id = id;
  }
}
