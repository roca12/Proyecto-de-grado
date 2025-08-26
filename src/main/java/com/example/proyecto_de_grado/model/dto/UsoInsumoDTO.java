package com.example.proyecto_de_grado.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsoInsumoDTO {
    private Integer idInsumo;
    private BigDecimal cantidad;
    private LocalDate fecha_uso; // Nueva propiedad
}

