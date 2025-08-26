package com.example.proyecto_de_grado.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UsoInsumoProduccionDTO {
    private Integer idInsumo;
    private BigDecimal cantidad;
    private LocalDate fechaUso;
}