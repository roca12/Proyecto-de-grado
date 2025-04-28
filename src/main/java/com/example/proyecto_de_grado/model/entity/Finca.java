package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

/**
 * Entidad que representa una finca dentro del sistema.
 *
 * <p>Contiene información básica como el nombre, la ubicación y el encargado de la finca.
 * Además, establece una relación con los usuarios que pertenecen a esta finca.</p>
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
    private String nombre;

    /** Ubicación geográfica de la finca. */
    private String ubicacion;

    /** Identificador del usuario encargado de la finca. */
    private String encargado;

    /** Lista de usuarios asociados a la finca. */
    @OneToMany(mappedBy = "finca", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}
