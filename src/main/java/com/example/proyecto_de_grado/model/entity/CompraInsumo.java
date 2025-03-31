package com.example.proyecto_de_grado.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "compra_insumo")
public class CompraInsumo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compra")
    private int idCompra;

    @ManyToOne
    @JoinColumn(name = "id_insumo", nullable = false)
    @NotNull(message = "El insumo es obligatorio.")
    private Insumo insumo;

    @Column(name = "cantidad", nullable = false, precision = 10, scale = 2)
    @Positive(message = "La cantidad debe ser mayor que cero.")
    private BigDecimal cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    @Positive(message = "El precio unitario debe ser mayor que cero.")
    private BigDecimal precioUnitario;

    @Column(name = "fecha_compra", nullable = false)
    @PastOrPresent(message = "La fecha de compra no puede ser futura.")
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "id_proveedor", nullable = false)
    @NotNull(message = "El proveedor es obligatorio.")
    private Proveedor proveedor;
}
