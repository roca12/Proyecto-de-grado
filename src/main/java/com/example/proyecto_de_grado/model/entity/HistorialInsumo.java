package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que registra el historial de uso de un insumo dentro de la finca.
 *
 * <p>Contiene información sobre el insumo utilizado, la cantidad empleada
 * y la fecha en la que se realizó el uso.</p>
 *
 * <p>Autor: Anderson Zuluaga</p>
 */
@Data
@Entity
@Table(name = "historial_insumo")
public class HistorialInsumo {

    /** Identificador único del registro de historial de insumo. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idHistorial;

    /** Insumo que fue utilizado (relación muchos a uno con {@link Insumo}). */
    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    /** Cantidad de insumo utilizada. Debe ser un valor positivo y con decimales. */
    @Column(name = "cantidad_utilizada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadUtilizada;

    /** Fecha y hora en la que se utilizó el insumo. */
    @Column(name = "fecha_uso", nullable = false)
    private LocalDateTime fechaUso;
}
