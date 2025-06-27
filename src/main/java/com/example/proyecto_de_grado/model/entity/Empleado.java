package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * Entidad que representa un empleado asociado a una finca dentro del sistema.
 *
 * <p>Contiene la relación con la entidad {@link Persona} y {@link Finca}, además de información
 * sobre el cargo, salario y fecha de contratación del empleado.
 *
 * <p>Autor: Anderson Zuluaga
 */
@Data
@Entity
@Table(name = "empleado")
public class Empleado {

    /** Identificador único del empleado. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private Integer idEmpleado;

    /** Persona asociada al empleado (relación muchos a uno). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    /** Finca a la cual está asociado el empleado (relación muchos a uno). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_finca", nullable = false)
    private Finca finca;

    /** Cargo del empleado dentro de la organización. */
    @Column(name = "cargo", nullable = false, length = 100)
    private String cargo;

    /** Salario del empleado. */
    @Column(name = "salario", nullable = false, precision = 10, scale = 2)
    private BigDecimal salario;

    /** Fecha en la que el empleado fue contratado. */
    @Column(name = "fecha_contratacion", nullable = false)
    private LocalDate fechaContratacion;
}