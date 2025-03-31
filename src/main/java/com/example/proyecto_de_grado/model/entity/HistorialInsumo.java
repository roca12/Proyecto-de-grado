package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "historial_insumo")
public class HistorialInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int idHistorial;

    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    private Insumo insumo;

    @Column(name = "cantidad_utilizada", nullable = false, precision = 10, scale = 2)
    private BigDecimal cantidadUtilizada;

    @Column(name = "fecha_uso", nullable = false)
    private LocalDateTime fechaUso;
}
