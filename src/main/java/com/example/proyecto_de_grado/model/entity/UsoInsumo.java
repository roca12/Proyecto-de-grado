package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "uso_insumo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsoInsumo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "id_actividad", nullable = false)
  private Actividad actividad;

  @ManyToOne
  @JoinColumn(name = "id_insumo", nullable = false)
  private Insumo insumo;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal cantidad;

  @Column(name = "fecha", nullable = false)
  private LocalDate fecha;
}
